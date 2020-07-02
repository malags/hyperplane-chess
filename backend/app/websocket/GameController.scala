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
}



