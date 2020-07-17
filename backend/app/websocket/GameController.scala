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

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import game.components.{Player, Point3D}
import javax.inject.Inject
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import services.GameService
import websocket.MatchManager.{Moved, NewClient, Remove}

class GameController @Inject()(cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer)
  extends AbstractController(cc) {
  val logger = Logger(this.getClass)

  /**
   * MatchManager: it manages all matches, a Match is a Game with ConnectedPlayerActors
   */
  val manager: ActorRef = system.actorOf(Props[MatchManager], "Manager")

  def newGame(): Action[AnyContent] = Action {
    val players: Array[Player] = Array(Player(0, 0), Player(1, 1))
    val movementFile: JsValue = Json.parse(
      """{
          "pieces": [
            {
              "name": "TEST_PIECE",
              "image": "nice image",
              "moveset": "(0,1,0),(1,1,0)"
            }
          ]
         }"""
    )
    val piecesPosition: String = """NA,NA,TEST_PIECE,TEST_PIECE,NA,NA,NA,TEST_PIECE,TEST_PIECE,NA,NA,NA,NA,NA,NA,NA,NA,NA"""
    val id = GameService.newGame(players, nrPlanes = 2, boardSize = 6, movementFile, piecesPosition)
    Ok(Json.obj("id" -> id))
  }

  def socket(id: Long): WebSocket = WebSocket.accept[JsValue, JsValue] { request =>
    ActorFlow.actorRef { out: ActorRef =>
      ConnectedPlayerActor.props(out, manager, id)
    }
  }

  def postNewGame() = Action { request =>
    val body: Option[String] = request.body.asText
    body match {
      case Some(txt) => {
        val jsonBody: JsValue = Json.parse(txt)
        //GameService.newGame(players,nrPlanes,boardSize,movementFile,piecesPositions)
        Ok("nice" + jsonBody.toString())
      }
      case None => BadRequest("no body")
    }

  }

}



