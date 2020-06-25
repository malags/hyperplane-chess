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

package game.components

import game.managers.MovementManager

/**
 * Class representing the game state, all boards in the game have the same size
 *
 * @param players   players participating in the game
 * @param nrPlanes  number of boards in the game
 * @param boardSize size of the side of the board
 */
class Game(players: Array[Player], nrPlanes: Int, boardSize: Int) {
  val boards: Boards = Boards(nrPlanes = nrPlanes, boardSize = boardSize)
  val movementManager: MovementManager = new MovementManager(null)
  val nrPlayers: Int = players.length
  var turnId: Int = 0


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
        if (turnId != piece.player.playerId)
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


}
