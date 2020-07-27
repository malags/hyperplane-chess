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

import akka.actor.{Actor, ActorRef}
import play.api.Logger
import play.api.libs.json.{JsObject, JsValue, Json}
import services.GameService
import websocket.MatchManager.{ChatMessage, Moved, NewClient, Remove}

/**
 * MatchManager manages groups of ConnectedPlayerActors (Matches) and notifies them when
 */
class MatchManager extends Actor {
  val logger = Logger(this.getClass)
  var participants: Map[Long, Set[ActorRef]] = Map.empty[Long, Set[ActorRef]].withDefaultValue(Set.empty[ActorRef])

  override def receive: Receive = {
    case ChatMessage(id: Long, message: JsValue) => {
      val response: JsObject = message.as[JsObject] + ("status" -> Json.toJson("ok"))
      participants.apply(id).foreach(
        client => client ! ChatMessage(id, response)
      )
    }
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
      logger.info("One player disconnected")
      //TODO enable
      //      if (clients.isEmpty) {
      //        GameService.remove(id)
      //        logger.info(s"deleted game $id")
      //      }
    }
    case default => logger.error(s"unhandled in Receive $default")
  }
}

object MatchManager {

  case class NewClient(client: ActorRef, id: Long)

  case class Moved(move: JsValue, id: Long)

  case class Remove(id: Long, client: ActorRef)

  case class ChatMessage(id: Long, message: JsValue)

}

//TODO: possibly add linked service to periodically check empty Games (created but not joined) and delete them
