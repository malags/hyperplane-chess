/*
 * Copyright © 2021 Stefano Malagò
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package game.components

import game.managers.{BoardConfigurator, MovementManager}
import play.api.libs.json.JsValue

/**
 * Class representing the game state, all boards in the game have the same size
 *
 * @param players   players participating in the game
 * @param nrPlanes  number of boards in the game
 * @param boardSize size of the side of the board
 */
class Game(players: Array[Player], nrPlanes: Int, boardSize: Int, movementFile: JsValue) {
  val movementManager: MovementManager = MovementManager(nrPlanes = nrPlanes, boardSize = boardSize, movementJson = movementFile)
  val boards: Boards = Boards(movementManager = movementManager)
  var turnId: Int = 0
  var playersAlive: Seq[Int] = Seq.range[Int](0, players.length)


  /**
   * Return a set with all possible moves for the piece in the given position considering game turn
   *
   * @param position position of the piece
   * @return set of possible moves for the piece in the given position, empty set if there was no piece
   */
  def movesAt(position: Point3D): Set[Point3D] = {
    boards.pieceAt(position) match {
      case Some(piece) =>
        // check game turn with piece ownership
        if (getPlayerTurnId != piece.player.playerId)
          Set.empty[Point3D]
        // owner can move
        else
          piece.availableMoves(this)
      case None => Set.empty[Point3D]
    }
  }

  /**
   * Given a position returns a Piece if present otherwise None
   *
   * @param position position of the wanted piece
   * @return the piece at the given position
   */
  def pieceAt(position: Point3D): Option[Piece] = boards.pieceAt(position)

  /**
   * Get PlayerId for current turn
   * @return
   */
  def getPlayerTurnId: Int = playersAlive(turnId)


  /**
   * update turnId to match next player
   */
  def endTurn(): Unit = turnId = (turnId + 1) % playersAlive.length


  /**
   * Set player as defeated (will skip turns)
   * @param player player that resigns
   */
  def setDefeatedPlayer(player: Player): Unit = {
    val currentPlayerTurn = getPlayerTurnId
    playersAlive = playersAlive.filter(p => p != player.playerId)

    // fix index issue if last player in playersAlive is defeated
    if (currentPlayerTurn == player.playerId && turnId == playersAlive.length - 1) turnId = 0

  }


  def init(boardConfigurator: BoardConfigurator): Boolean = {
    if (!boardConfigurator.verify(boards))
      false
    else {
      boardConfigurator.initBoards(boards, players)
      true
    }
  }

  /**
   * Expose movePiece method from boards
   * Move piece regardless of whether the start/target position are free or occupied by any piece
   *
   * @param start  start position
   * @param target end position
   */
  def movePiece(start: Point3D, target: Point3D): Unit = {
    boards.movePiece(start, target)
  }
}
