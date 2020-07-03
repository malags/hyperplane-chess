package websocket

import akka.actor.{Actor, ActorRef, Props}
import game.components.Facing
import game.components.{Piece, Player, Point3D, Type}
import play.api.Logger
import play.api.libs.json.{JsError, JsPath, JsSuccess, JsValue, Json, Reads, Writes}
import services.GameService
import websocket.MatchManager.Moved

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
  logger.info(s"new client with $id")
  manager ! MatchManager.NewClient(self, id)


  /**
   * Defines ConnectedPlayerActor action depending on the received message
   *
   * @return
   */
  override def receive: Receive = {
    case Moved(move, id) => out ! move

    // initial case
    case value: JsValue =>
      val command = (value \ "command").get.as[String]
      logger.info(command)

      command match {
        case "submitMove" => submitMoveAction(value)
        case "getAvailableMoves" => availableMovesAction(value)
        case "getGameStatus" => gameStatusAction()
        case _ =>
      }

    case default => logger.error(s"unhandled in Receive $default")
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
    val from = (data \ "from").get.as[Array[Int]]
    val to = (data \ "to").get.as[Array[Int]]

    val status = GameService.submitMove(id, Point3D.arrayToPoint3D(from), Point3D.arrayToPoint3D(to), Player(0, 0))

    // send result back
    out ! Json.obj("status" -> (if (status) "ok" else "failed"))

    // notify other clients of updated Game status
    if (status) manager ! MatchManager.Moved(request, id)
  }

  private def availableMovesAction(request: JsValue): Unit = {
    logger.info("getAvailableMovesAction")
    // do stuff
    val data = (request \ "data").get
    val from = (data \ "from").get.as[Array[Int]]

    val playerResult = Json.fromJson[Player]((data \ "player").get)

    playerResult match {
      case JsSuccess(player, _) =>

        val pos = Point3D.arrayToPoint3D(from)
        val result = GameService.getAvailableMoves(id, pos, player)

        out ! Json.obj(
          "status" -> "ok",
          "data" -> result.map(point => Array(point.x, point.y, point.z))
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
    //TODO: enable when testing over
    //manager ! MatchManager.Remove(id, self)
  }

  /**
   * send a Failed notification to websocket
   */
  private def failed(): Unit = out ! Json.obj("status" -> "failed")

}

object ConnectedPlayerActor {
  def props(out: ActorRef, manager: ActorRef, id: Long): Props = Props(new ConnectedPlayerActor(out, manager, id))
}
