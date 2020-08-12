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

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import javax.inject.Inject
import play.api.Logger
import play.api.libs.json.{JsLookupResult, JsValue, Json}
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import services.{GameBuilderService}

class GameController @Inject()(cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer)
  extends AbstractController(cc) {
  val logger = Logger(this.getClass)

  /**
   * MatchManager: it manages all matches, a Match is a Game with ConnectedPlayerActors
   */
  val manager: ActorRef = system.actorOf(Props[MatchManager], "Manager")

  def newGame(): Action[AnyContent] = Action {
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
    val piecesPosition: List[String] = List("", "", "TEST_PIECE", "TEST_PIECE", "", "", "", "TEST_PIECE", "TEST_PIECE", "", "", "", "", "", "", "", "", "")
    val id = GameBuilderService.newBuilder(nrPlanes = 2, nrPlayers = 2, boardSize = 6, movementFile, piecesPosition)
    Redirect(s"/connect/$id")
  }

  def socket(id: Long): WebSocket = WebSocket.accept[JsValue, JsValue] { request =>
    ActorFlow.actorRef { out: ActorRef =>
      ConnectedPlayerActor.props(out, manager, id)
    }
  }

  def postNewGame(): Action[AnyContent] = Action { request =>
    val body: Option[String] = request.body.asText
    body match {
      case Some(txt) => {
        val jsonBody: JsValue = Json.parse(txt)

        val jsNrPlanes: JsLookupResult = jsonBody \ "nrBoards"
        val jsNrPlayers: JsLookupResult = jsonBody \ "nrPlayers"
        val jsBoardsSize: JsLookupResult = jsonBody \ "boardSize"
        val jsMovementFile: JsLookupResult = jsonBody \ "movementFile"
        val jsPiecesPositions: JsLookupResult = jsonBody \ "piecesPosition"
        if (jsNrPlanes.isDefined &&
          jsNrPlayers.isDefined &&
          jsBoardsSize.isDefined &&
          jsMovementFile.isDefined &&
          jsPiecesPositions.isDefined) {

          try {
            val mov = jsMovementFile.get
            // GameBuilder service
            val id: Long = GameBuilderService.newBuilder(
              jsNrPlanes.as[Int],
              jsNrPlayers.as[Int],
              jsBoardsSize.as[Int],
              mov,
              jsPiecesPositions.as[List[String]]
            )

            Ok(Json.obj("url" -> s"/connect/$id"))
          }
          catch {
            case e: Throwable => BadRequest(e.getMessage)
          }
        }
        else BadRequest("invalid JSON at postNewGame")
      }
      case None => BadRequest("no body")
    }

  }

}



