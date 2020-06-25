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

package game.managers

import game.components.{Direction, Piece, Point3D, Type}

class MovementManager(movementFile: Object) {
  var movementMap: Map[Type, Array[Direction]] = scala.collection.immutable.Map[Type, Array[Direction]]()
  init()

  def init() {
    //TODO configure various pieces according to to be defined format
    movementMap = movementMap + (
      Type("none") -> Array(
        Direction(Array(Point3D(0, 1, 0))))
      )
  }

  /**
   * get moveset corresponding to pieceType, empty moveset if none
   *
   * @param piece piece for which we want to know the moveset
   * @return moveset of the piece
   */
  def getMoveset(piece: Piece): Array[Direction] = movementMap.getOrElse(piece.pieceType, Array.empty[Direction])
}
