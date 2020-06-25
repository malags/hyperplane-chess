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


import scala.util.control._

/**
 *
 * @param player    owner of the piece
 * @param pieceType String representing the type of the Piece (i.e. King, Knight)
 * @param position  the current position of the Piece
 */
case class Piece(player: Player, pieceType: Type, position: Point3D) {
  /**
   * given a target player returns whether pieces of the target player could be eaten by the current piece
   *
   * @param target player owning the piece to be eaten
   * @return
   */
  def isEnemy(target: Player): Boolean = player.groupId != target.groupId

  /**
   * computes the available moves for the piece, moves include eat action
   *
   * @return set of positions in which we could move the Piece
   */
  def availableMoves(game: Game): Set[Point3D] = {
    val loop = new Breaks
    val moveset: Array[Direction] = game.movementManager.getMoveset(this)
    var availableMoves = Set.empty[Point3D]

    // check all directions
    moveset.foreach(
      direction => {
        loop.breakable {
          // check points inside direction
          for (point <- direction.dir) {
            val targetPosition = position.add(point, game.boards)
            val piece = game.pieceAt(targetPosition)
            if (piece.isEmpty || this.isEnemy(piece.get.player))
              availableMoves += targetPosition
            else
              loop.break
          }
        }
      }
    )
    availableMoves
  }

}
