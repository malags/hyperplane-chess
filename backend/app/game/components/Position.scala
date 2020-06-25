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

/**
 *
 * @param x the x position in the Board [0, boardSize]
 * @param y the y position in the Board [0, boardSize]
 * @param z the board in which the piece is placed [0, nrPlanes]
 */
case class Point3D(x: Int, y: Int, z: Int) {

  /**
   * Add a direction to the current Point3D to obtain a Point3D shifted in direction dir
   *
   * @param dir    direction in which to move the point
   * @param boards game Boards
   * @return this point moved in direction dir
   */
  def add(dir: Point3D, boards: Boards): Point3D = Point3D(
    x + dir.x,
    y + dir.y,
    // inner % scales back to single loop, + handles negative, outer % scales back to range from negative handling
    (z + (dir.z % boards.nrPlanes) + boards.nrPlanes) % boards.nrPlanes)

  def isOutOfBounds(boards: Boards): Boolean = {
    x >= boards.boardSize ||
      y >= boards.boardSize ||
      z >= boards.nrPlanes ||
      x < 0 ||
      y < 0 ||
      z < 0
  }
}

/**
 *
 * @param dir an array of Point3D following a direction, size >= 1
 */
case class Direction(dir: Array[Point3D])
