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
 * Class representing all the boards in a game
 *
 * @param movementManager The movement Manager of the game
 */
case class Boards(movementManager: MovementManager) {
  var boards: Seq[Piece] = Seq.empty[Piece]

  /**
   *
   * @param target position to check in the board (x,y,z), where z is the board
   */
  def pieceAt(target: Point3D): Option[Piece] = {
    if (target.isOutOfBounds(movementManager))
      None
    else
      boards.find(piece => piece.position.equals(target))
  }


  /**
   * Add the given piece to the board
   *
   * @param piece piece to add to the board
   */
  def addPiece(piece: Piece): Unit = boards = boards :+ piece

  /**
   *
   * @param start  starting position of the piece to move
   * @param target end position of the piece to move
   */
  def movePiece(start: Point3D, target: Point3D): Unit = {
    val pieceToMove = pieceAt(start).get
    val movingPiece = Piece(
      player = pieceToMove.player,
      pieceType = pieceToMove.pieceType,
      position = target,
      facing = pieceToMove.facing)

    // remove pieces in start and target position, then add update piece
    boards = boards.filter(
      piece => !(piece.position == start) && !(piece.position == target)
    ) :+ movingPiece
  }
}
