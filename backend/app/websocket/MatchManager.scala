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
import game.components.Player
import play.api.Logger
import play.api.libs.json.{JsObject, JsValue, Json}
import utils.JsonUtils._
import websocket.MatchManager.{ChatMessage, Moved, NewClient, Ready, Remove, SetPlayer}

/**
 * MatchManager manages groups of ConnectedPlayerActors (Matches) and notifies them when
 */
class MatchManager extends Actor {
  val logger = Logger(this.getClass)
  var participants: Map[Long, Set[ActorRef]] = Map.empty[Long, Set[ActorRef]].withDefaultValue(Set.empty[ActorRef])

  private def addStatusOk(request: JsValue): JsObject = request.as[JsObject] + ("status" -> Json.toJson("ok"))

  override def receive: Receive = {
    case ChatMessage(id: Long, message: JsValue) => participants.apply(id).foreach(
      _ ! ChatMessage(id, addStatusOk(message))
    )

    case NewClient(id: Long, client: ActorRef) => {
      val clients = participants.apply(id) + client
      participants = participants + (id -> clients)
    }
    case moved@Moved(id: Long, move: JsValue) => {
      logger.info(s"moved $move")
      participants.apply(id).foreach(_ ! moved)
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

    case ready@Ready(id: Long, _) => {
      // inform clients
      participants.apply(id).foreach(_ ! ready)
      // if can build, do so (maybe)
    }

    case setPlayer@SetPlayer(id: Long, _) => participants.apply(id).foreach(_ ! setPlayer)

    case default => logger.error(s"unhandled in Receive $default")
  }
}

object MatchManager {

  case class NewClient(id: Long, client: ActorRef)

  case class Moved(id: Long, move: JsValue)

  case class Remove(id: Long, client: ActorRef)

  case class ChatMessage(id: Long, message: JsValue)

  case class Ready(id: Long, message: JsValue)

  case class SetPlayer(id: Long, request: JsValue)

}

//TODO: possibly add linked service to periodically check empty Games (created but not joined) and delete them
