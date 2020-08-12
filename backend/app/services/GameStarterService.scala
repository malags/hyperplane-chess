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

package services

import java.util.concurrent.ConcurrentHashMap

import akka.actor.ActorRef
import play.api.Logger
import websocket.MatchManager

import scala.collection.JavaConverters._
import scala.collection.concurrent
import java.util.TimerTask
import java.util.Timer
import java.util.concurrent.atomic.AtomicBoolean

object GameStarterService {
  val logger = Logger(this.getClass)
  val mapIdToGameStarter: concurrent.Map[Long, GameStarter] = new ConcurrentHashMap[Long, GameStarter]().asScala

  def newGamerStarter(id: Long, manager: ActorRef): Unit = {
    val gameStarter: GameStarter = GameStarter(id, manager)
    mapIdToGameStarter.put(id, gameStarter)
  }

  def startCountDown(id: Long): Unit = mapIdToGameStarter.apply(id).startCountdown()

  def stopCountDown(id: Long): Unit = mapIdToGameStarter.apply(id).stopCountdown()

  def remove(id: Long): Unit = mapIdToGameStarter.remove(id)

  def exists(id: Long): Boolean = mapIdToGameStarter.contains(id)


  case class GameStarter(id: Long, manager: ActorRef) {
    val task = new TimerTask() {
      override def run(): Unit = {
        GameBuilderService.build(id)
        GameStarterService.remove(id)
        manager ! MatchManager.StartGame(id)
      }
    }

    var running: AtomicBoolean = new AtomicBoolean(false)

    var timer: Timer = null


    def startCountdown(): Unit = {
      if (!running.get()) {
        running.set(true)
        timer = new Timer()
        timer.schedule(task, 5000)
      }
    }

    def stopCountdown(): Unit = {
      if (running.get()) {
        timer.cancel()
        timer.purge()
        running.set(false)
      }
    }

  }

}
