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


import scala.collection._
import scala.collection.JavaConverters._
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

import game.components.{Game, Piece, Player, Point3D}
import game.managers.BoardConfigurator
import play.api.libs.json.JsValue


object GameService {
  val mapIdToGame: concurrent.Map[Long, Game] = new ConcurrentHashMap[Long, Game]().asScala
  val n = new AtomicLong(0L)


  /**
   * Create a new Game as specified by given input, return the ID required to interact with the game
   *
   * @param players        players in the Game
   * @param nrPlanes       number of planes in the Game
   * @param boardSize      board size in the Game
   * @param movementFile   Json representing the pieces in the game and their moveset
   * @param piecesPosition CSV (no header) representing the starting position of the boards
   * @return ID of the Game
   */
  def newGame(players: Array[Player], nrPlanes: Int, boardSize: Int, movementFile: JsValue, piecesPosition: List[String], id: Long): Long = {

    val game: Game = new Game(players, nrPlanes, boardSize, movementFile)
    val boardConfigurator: BoardConfigurator = BoardConfigurator(piecesPosition)

    if (!game.init(boardConfigurator)) throw new IllegalArgumentException("Invalid format for pieces position")

    mapIdToGame.put(id, game)
    id
  }

  /**
   * Book an ID for a Game
   *
   * @return the ID of the Game
   */
  def requestId(): Long = n.getAndIncrement()


  /**
   * Validates the move with respect to the player and game,<br>
   * if the move is accepted it is executed, the turn is passed and true is returned<br>
   * if the move is rejected false is returned, a rejection could be caused by:
   * <ul>
   * <li>No game found with given {@code gameID}</li>
   * <li>No piece at {@code from} position</li>
   * <li>{@code to} is not one of the valid moves of the piece at position {@code from}</li>
   * <li>the given {@code player} cannot move during the current game turn</li>
   * </ul>
   *
   * @param gameId ID of the game as generated in newGame
   * @param from   position of the piece to move
   * @param to     target position of the piece to move
   * @param player player submitting the move
   * @return whether the move was accepted or not
   */
  def submitMove(gameId: Long, from: Point3D, to: Point3D, player: Player): Boolean = {
    // TODO: move some logic to Game
    mapIdToGame.get(gameId) match {
      case None => false
      case Some(game) =>
        if (game.movesAt(from).isEmpty) false
        else {
          // game is present, piece at from position could move in this turn
          val piece: Piece = game.pieceAt(from).get
          if (piece.player == player && piece.availableMoves(game).contains(to)) {
            game.movePiece(from, to)
            game.endTurn()
            true
          }
          else false
        }
    }
  }


  /**
   * Get the Set of moves the player could make if he were to move the piece at pos
   *
   * @param gameId ID of the game as generated in newGame
   * @param pos    position of the piece
   * @param player player requesting the available moves
   * @return Set of available moves for the player using the piece at pos
   */
  def getAvailableMoves(gameId: Long, pos: Point3D, player: Player): Set[Point3D] = {
    mapIdToGame.get(gameId) match {
      case None => Set.empty[Point3D]
      case Some(game) =>
        game.pieceAt(pos) match {
          case None => Set.empty[Point3D]
          case Some(piece) => if (piece.player == player) game.movesAt(pos) else Set.empty[Point3D]
        }
    }
  }

  /**
   *
   * @param gameId ID of the game as generated in newGame
   * @return The sequence of Piece relative to the game with gameId
   */
  def getPiecesInBoard(gameId: Long): Option[Seq[Piece]] = mapIdToGame.get(gameId) match {
    case None => None
    case Some(game) => Some(game.boards.boards)
  }

  /**
   *
   * @param gameId ID of the game as generated in newGame
   * @return The nrPlanes relative to the game with gameId
   */
  def getNrPlanes(gameId: Long): Option[Int] = mapIdToGame.get(gameId) match {
    case None => None
    case Some(game) => Some(game.movementManager.nrPlanes)
  }

  /**
   *
   * @param gameId ID of the game as generated in newGame
   * @return The boardSize relative to the game with gameId
   */
  def getBoardSize(gameId: Long): Option[Int] = mapIdToGame.get(gameId) match {
    case None => None
    case Some(game) => Some(game.movementManager.boardSize)
  }

  /**
   * Remove the Game with specified id
   *
   * @param gameId Game to remove
   */
  def remove(gameId: Long): Unit = mapIdToGame.remove(gameId)
}
