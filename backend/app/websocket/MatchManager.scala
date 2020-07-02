package websocket

import akka.actor.{Actor, ActorRef}
import play.api.Logger
import play.api.libs.json.JsValue
import services.GameService
import websocket.MatchManager.{Moved, NewClient, Remove}

/**
 * MatchManager manages groups of ConnectedPlayerActors (Matches) and notifies them when
 */
class MatchManager extends Actor {
  val logger = Logger(this.getClass)
  var participants: Map[Long, Set[ActorRef]] = Map.empty[Long, Set[ActorRef]].withDefaultValue(Set.empty[ActorRef])

  override def receive: Receive = {
    case NewClient(client: ActorRef, id: Long) => {
      val clients = participants.apply(id) + client
      participants = participants + (id -> clients)
    }
    case Moved(move: JsValue, id: Long) => {
      logger.info(s"moved $move")
      participants.apply(id).foreach(client => client ! Moved(move, id))
    }
    case Remove(id: Long, client: ActorRef) => {
      val clients = participants.apply(id).filter(_ != client)
      participants = participants + (id -> clients)
      if (clients.isEmpty) {
        GameService.remove(id)
        logger.info(s"deleted game $id")
      }
    }
    case default => logger.error(s"unhandled in Receive $default")
  }
}

object MatchManager {

  case class NewClient(client: ActorRef, id: Long)

  case class Moved(move: JsValue, id: Long)

  case class Remove(id: Long, client: ActorRef)

}

//TODO: possibly add linked service to periodically check empty Games (created but not joined) and delete them
