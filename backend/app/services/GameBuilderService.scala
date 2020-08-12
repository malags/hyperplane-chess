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

import scala.collection.JavaConverters._
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

import game.components.Player
import play.api.Logger
import play.api.libs.json.JsValue

import scala.collection.concurrent


object GameBuilderService {
  val logger = Logger(this.getClass)

  val mapIdToGameBuilder: concurrent.Map[Long, GameBuilder] = new ConcurrentHashMap[Long, GameBuilder]().asScala

  /**
   *
   * @param nrPlanes       nr of planes of the Game to build
   * @param boardSize      board size of the Game to build
   * @param movementFile   movementFile of the Game to build
   * @param piecesPosition piecesPosition of the Game to build
   */
  def newBuilder(nrPlanes: Int, nrPlayers: Int, boardSize: Int, movementFile: JsValue, piecesPosition: List[String]): Long = {
    logger.info("newBuilder")
    val id: Long = GameService.requestId()
    val gameBuilder: GameBuilder = GameBuilder(
      nrPlanes = nrPlanes,
      nrPlayers = nrPlayers,
      boardSize = boardSize,
      movementFile = movementFile,
      piecesPosition = piecesPosition,
      id = id)
    mapIdToGameBuilder.put(id, gameBuilder)
    id
  }

  def setGroupForPlayer(id: Long, playerId: Int, groupId: Int, name: String): Unit = mapIdToGameBuilder.apply(id)
    .setGroupForPlayer(playerId, groupId, name)

  def setPlayer(id: Long, player: Player): Unit = mapIdToGameBuilder.apply(id).setPlayer(player)

  /**
   * Create a new player for the given gameId if the game is not full
   *
   * @param id gameId
   * @return Player
   */
  def newPlayer(id: Long): Option[Player] = mapIdToGameBuilder.apply(id).newPlayer()

  def getAllPlayers(id: Long) = mapIdToGameBuilder.apply(id).players.filter(player => player != null)

  def setReady(id: Long, player: Player, readyStatus: Boolean): Unit = mapIdToGameBuilder.apply(id)
    .setReady(player, readyStatus)

  def getAllReadyStatus(id: Long): Array[Boolean] = mapIdToGameBuilder.apply(id).ready


  def build(id: Long) = {
    logger.info(s"build $id")
    mapIdToGameBuilder.apply(id).build()
    mapIdToGameBuilder.remove(id)
    id
  }


  case class GameBuilder(nrPlanes: Int, nrPlayers: Int, boardSize: Int, movementFile: JsValue, piecesPosition: List[String], id: Long) {
    val logger = Logger(this.getClass)
    val players: Array[Player] = Array.ofDim[Player](nrPlayers)
    val ready: Array[Boolean] = Array.ofDim[Boolean](nrPlayers)

    var playerIdCounter: AtomicInteger = new AtomicInteger(0)

    def setGroupForPlayer(playerId: Int, groupId: Int, name: String): Unit = {
      logger.info(s"setGroupForPlayer playerId=$playerId groupId=$groupId")
      val player: Player = Player(playerId, groupId, name)
      players(playerId) = player
    }

    def setPlayer(player: Player): Unit = players(player.playerId) = player

    def setReady(player: Player, readyStatus: Boolean): Unit = {
      ready(player.playerId) = readyStatus
      if (!ready.contains(false)) GameStarterService.startCountDown(id)
      else GameStarterService.stopCountDown(id)
    }

    /**
     * Create a new player for the game being built
     *
     * @return the created Player
     */
    def newPlayer(): Option[Player] = {
      logger.info("created new player")
      if (playerIdCounter.get() == players.length)
        None
      else {
        val player: Option[Player] = Some(Player(
          playerId = playerIdCounter.getAndIncrement(),
          groupId = 0,
          name = ""
        ))
        setPlayer(player.get)
        player
      }
    }

    /**
     * Create a new Game in GameService
     *
     * @return ID of the created game
     */
    def build(): Long = GameService.newGame(players, nrPlanes, boardSize, movementFile, piecesPosition, id)
  }

}
