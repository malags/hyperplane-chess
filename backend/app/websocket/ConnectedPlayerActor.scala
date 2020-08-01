/*
 * Copyright (c) 2020 Stefano Malagò
 * Copyright (c) 2013-2017 Mathew Groves, Chad Engler
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package websocket

import akka.actor.{Actor, ActorRef, Props}
import game.components.Facing
import game.components.{Piece, Player, Point3D, Type}
import play.api.Logger
import play.api.libs.json.{JsError, JsNull, JsPath, JsSuccess, JsValue, Json, Reads, Writes}
import services.GameService
import websocket.MatchManager.{ChatMessage, Moved, Ready}

/**
 * Actor containing information on the GameId
 * This actor represents a single player and is used to link player actions on the UI and effects in the Game
 *
 * @param out     Actor
 * @param manager MatchManager of all actors
 * @param id      GameId of the Game the player is playing
 */
class ConnectedPlayerActor(out: ActorRef, manager: ActorRef, id: Long) extends Actor {

  private implicit val playerReads: Reads[Player] = Json.reads[Player]
  private implicit val playerWrites: Writes[Player] = Json.writes[Player]

  private implicit val point3DReads: Reads[Point3D] = Json.reads[Point3D]
  private implicit val point3DWrites: Writes[Point3D] = Json.writes[Point3D]

  private implicit val typeReads: Reads[Type] = Json.reads[Type]
  private implicit val typeWrites: Writes[Type] = Json.writes[Type]

  private implicit val facingReads: Reads[Facing.Value] = Reads.enumNameReads(Facing)
  private implicit val facingWrites: Writes[Facing.Value] = Writes.enumNameWrites

  private implicit val pieceReads: Reads[Piece] = Json.reads[Piece]
  private implicit val pieceWrites: Writes[Piece] = Json.writes[Piece]

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
    case Moved(id, move) => gameStatusAction()
    case ChatMessage(id, message) => out ! message
    case Ready(id, client, request) => ???

    //TODO: add changes to builder and build on ready

    // initial case
    case value: JsValue =>
      val command = (value \ "command").getOrElse(Json.toJson("No command in Json")).as[String]
      logger.info(command)

      command match {
        case "ping" => out ! Json.obj("status" -> "pong")
        case "message" => sendMessage(value)
        case "getAvailableMoves" => availableMovesAction(value)
        case "submitMove" => submitMoveAction(value)
        case "getGameStatus" => gameStatusAction()
        case "ready" => setReady(value)
        case _ => logger.warn("unhandled message")
      }

    case default: Any => logger.error(s"unhandled in Receive $default")
  }

  def setReady(request: JsValue) = {
    manager ! MatchManager.Ready(id, self, request)
  }

  def sendMessage(request: JsValue): Unit = {
    logger.info("sendMessage")
    manager ! MatchManager.ChatMessage(id, request)
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
    var status = false

    (fromJs, toJs) match {
      case (JsSuccess(from, _), JsSuccess(to, _)) =>
        status = GameService.submitMove(id, from, to, Player(0, 0))
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

        out ! Json.obj(
          "status" -> "ok",
          "command" -> "availableMoves",
          "data" -> result
        )

      case _ => failed()
    }
  }


  private def gameStatusAction(): Unit = {
    logger.info("getGameStatusAction")
    // do stuff
    val optionPieces = GameService.getPiecesInBoard(id)

    optionPieces match {
      case Some(pieces) =>

        out ! Json.obj(
          "status" -> "ok",
          "command" -> "gameStatus",
          "data" -> Json.obj(
            "nrPlanes" -> GameService.getNrPlanes(id).get, // has some value because Some(pieces)
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

}

object ConnectedPlayerActor {
  def props(out: ActorRef, manager: ActorRef, id: Long): Props = Props(new ConnectedPlayerActor(out, manager, id))
}
