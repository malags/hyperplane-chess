/*
 * Copyright © 2021 Stefano Malagò
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package websocket

import akka.actor.{Actor, ActorRef, Props}
import utils.JsonUtils._
import game.components.{Player, Point3D}
import play.api.Logger
import play.api.libs.json.{JsError, JsNull, JsResult, JsSuccess, JsValue, Json}
import services.{GameBuilderService, GameService}
import websocket.MatchManager.{ChatMessage, Moved, Ready, SetPlayer, StartGame}
import websocket.ActorCommand._

/**
 * Actor containing information on the GameId
 * This actor represents a single player and is used to link player actions on the UI and effects in the Game
 *
 * @param out     Actor
 * @param manager MatchManager of all actors
 * @param id      GameId of the Game the player is playing
 */
class ConnectedPlayerActor(out: ActorRef, manager: ActorRef, id: Long) extends Actor {

  val logger: Logger = Logger(this.getClass)
  // inform manager of new actor
  logger.info(s"new client with gameId=$id  $self")
  manager ! MatchManager.NewClient(id, self)


  /**
   * Defines ConnectedPlayerActor action depending on the received message
   *
   * @return
   */
  override def receive: Receive = {
    case Moved(_, _) => gameStatusAction()
    case ChatMessage(_, message) => out ! message
    case Ready(_, message) => out ! message
    case SetPlayer(_, response) => out ! response
    case StartGame(_) => success(command = "gameStart", data = JsNull)

    // initial case
    case value: JsValue =>
      val command = ActorCommand.toCommand(
        (value \ "command").getOrElse(Json.toJson("No command in Json")).as[String]
      )
      logger.info(command.toString)

      command match {
        case PING => out ! Json.obj("status" -> "pong")
        case MESSAGE => sendMessage(value)
        case GET_AVAILABLE_MOVES => availableMovesAction(value)
        case SUBMIT_MOVE => submitMoveAction(value)
        case GET_GAME_STATUS => gameStatusAction()
        case READY => setReady(value)
        case SET_PLAYER => setPlayer(value)
        case NEW_PLAYER => newPlayer()
        case GET_ALL_PLAYERS => getAllPlayers()
        case GET_ALL_READY_STATUS => out ! getAllReadyStatusJsValue
        case PASS => passAction(value)
        case RESIGN => resignAction(value)
        case UNKNOWN => logger.warn(s"unhandled message $command")
      }

    case default: Any => logger.error(s"unhandled in Receive $default")
  }

  private def getAllReadyStatusJsValue: JsValue = {
    Json.obj(
      "status" -> "ok",
      "command" -> "getAllReadyStatus",
      "data" -> GameBuilderService.getAllReadyStatus(id)
    )
  }

  private def getAllPlayers(): Unit = {
    GameBuilderService.getAllPlayers(id).foreach(
      player => success(
        command = "setPlayer",
        data = Json.toJson(player)
      )
    )
  }

  private def setReady(request: JsValue): Unit = {
    (
      Json.fromJson[Player]((request \ "data" \ "player").get),
      Json.fromJson[Boolean]((request \ "data" \ "ready").get)
    ) match {
      case (JsSuccess(player, _), JsSuccess(ready, _)) =>
        GameBuilderService.setReady(id, player, ready)
        manager ! MatchManager.Ready(id, getAllReadyStatusJsValue)
      case _ => failed("setReady")
    }
  }

  private def setPlayer(request: JsValue): Unit = {
    val data = (request \ "data").get
    Json.fromJson[Player](data) match {
      case JsSuccess(player, _) =>
        GameBuilderService.setPlayer(id, player)
        val response: JsValue = Json.obj(
          "status" -> "ok",
          "command" -> "setPlayer",
          "data" -> data
        )
        manager ! MatchManager.SetPlayer(id, response)
      case JsError(_) => failed("setPlayer")
    }
  }

  private def sendMessage(request: JsValue): Unit = {
    logger.info("sendMessage")
    manager ! MatchManager.ChatMessage(id, request)
  }

  private def sendServerMessage(message: String): Unit = {
    logger.info("sendServerMessage")
    manager ! MatchManager.ChatMessage(id, Json.obj(
      "command" -> "message",
      "data" -> Json.obj(
        "id" -> "server",
        "content" -> message,
        "sender" -> "Server"
      )
    ))
  }

  private def newPlayer(): Unit = {
    val player = GameBuilderService.newPlayer(id)
    if (player.nonEmpty) {
      success(
        command = "newPlayer",
        data = Json.toJson(player.get)
      )
      manager ! MatchManager.SetPlayer(id, Json.obj(
        "status" -> "ok",
        "command" -> "setPlayer",
        "data" -> player.get
      ))
    }
    else out ! Json.obj(
      "status" -> "failed",
      "command" -> "newPlayer"
    )
  }

  private def getPlayer(request: JsValue): JsResult[Player] = {
    val data = (request \ "data").get
    Json.fromJson[Player]((data \ "player").get)
  }


  private def passAction(request: JsValue): Unit = {
    getPlayer(request) match {
      case JsSuccess(player, _) =>
        GameService.passTurn(id, player)
        sendServerMessage("Passed Turn.")
      case _ =>
    }
  }


  private def resignAction(request: JsValue): Unit = {
    getPlayer(request) match {
      case JsSuccess(player, _) =>
        GameService.setDefeatedPlayer(id, player)
        sendServerMessage(s"Player ${player.name} is defeated.")
      case _ =>
    }
  }


  /**
   * Manipulate message, notify client for moved
   *
   * @param request raw request
   */
  private def submitMoveAction(request: JsValue): Unit = {
    logger.info("submitMoveAction")
    // do stuff
    val data = (request \ "data").get
    val fromJs = Json.fromJson[Point3D]((data \ "from").get)
    val toJs = Json.fromJson[Point3D]((data \ "to").get)
    val playerJs = Json.fromJson[Player]((data \ "player").get)
    var status = false

    (fromJs, toJs, playerJs) match {
      case (JsSuccess(from, _), JsSuccess(to, _), JsSuccess(player, _)) =>
        status = GameService.submitMove(id, from, to, player)
      case _ =>
    }

    // send result back
    out ! Json.obj("status" -> (if (status) "ok" else "failed"))

    // notify other clients of updated Game status
    if (status) manager ! MatchManager.Moved(id, request)
  }

  private def availableMovesAction(request: JsValue): Unit = {
    logger.info("getAvailableMovesAction")
    // do stuff
    val data = (request \ "data").get
    val from = Json.fromJson[Point3D]((data \ "from").get)

    val playerResult = Json.fromJson[Player]((data \ "player").get)

    (playerResult, from) match {
      case (JsSuccess(player, _), JsSuccess(pos, _)) =>
        val result = GameService.getAvailableMoves(id, pos, player)
        success(
          command = "availableMoves",
          data = Json.toJson(result)
        )
      case _ => failed("availableMoves")
    }
  }


  private def gameStatusAction(): Unit = {
    logger.info("getGameStatusAction")
    // do stuff
    val optionPieces = GameService.getPiecesInBoard(id)

    optionPieces match {
      case Some(pieces) =>
        success(
          command = "gameStatus",
          data = Json.obj(
            "nrBoards" -> GameService.getNrPlanes(id).get, // has some value because Some(pieces)
            "boardSize" -> GameService.getBoardSize(id).get, // has some value because Some(pieces)
            "pieces" -> pieces
          )
        )
      case None => failed()
    }

  }

  /**
   * remove self from MatchManager
   */
  override def postStop(): Unit = {
    manager ! MatchManager.Remove(id, self)
  }

  /**
   * send a Failed notification to websocket
   */
  private def failed(): Unit = out ! Json.obj("status" -> "failed")

  /**
   * send a Failed notification to websocket with given command
   *
   * @param command command of the failed request
   */
  private def failed(command: String): Unit = out ! Json.obj("status" -> "failed", "command" -> command)

  private def success(command: String, data: JsValue): Unit = out ! Json.obj(
    "status" -> "ok",
    "command" -> command,
    "data" -> data)
}

object ConnectedPlayerActor {
  def props(out: ActorRef, manager: ActorRef, id: Long): Props = Props(new ConnectedPlayerActor(out, manager, id))
}
