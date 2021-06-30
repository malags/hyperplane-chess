/*
 * Copyright © 2021 Stefano Malagò
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package game.managers

import game.components.{Direction, Piece, Point3D, Type}
import play.api.libs.json.JsValue

/**
 *
 * @param nrPlanes     number of distinct planes (boards) to have
 * @param boardSize    size of one side of the board (in normal chess the value would be 8)
 * @param movementJson JsValue representing the moveset for each piece
 */
case class MovementManager(nrPlanes: Int, boardSize: Int, movementJson: JsValue) {

  import play.api.libs.json._

  // Required boilerplate to read Json Value to class
  private case class PieceDeclaration(name: String, image: String, moveset: String)

  private case class PiecesArray(pieces: Array[PieceDeclaration])

  private implicit val pieceDeclarationReads: Reads[PieceDeclaration] = Json.reads[PieceDeclaration]
  private implicit val piecesArrayReads: Reads[PiecesArray] = Json.reads[PiecesArray]

  var movementMap: Map[Type, Array[Direction]] = scala.collection.immutable.Map[Type, Array[Direction]]()

  init()


  private def init() {

    val pieces: JsResult[PiecesArray] = Json.fromJson[PiecesArray](movementJson)
    val regex = """(?<=\()[^)]+(?=\))""".r

    pieces match {
      case JsSuccess(json: PiecesArray, path: JsPath) =>
        json.pieces.foreach(piece => {
          val moves = regex.findAllIn(piece.moveset)

          // all moves of the piece
          var moveset = Seq.empty[Direction]

          // extract single move
          while (moves.hasNext) {
            val move = moves.next().split(",")
            moveset = moveset :+ convertToDirection(x = move(0), y = move(1), z = move(2))
          }
          movementMap = movementMap + (Type(piece.name) -> moveset.toArray)

        })
      case e: JsError => throw new IllegalArgumentException("Errors: " + JsError.toJson(e).toString())

    }

  }

  private def convertToDirection(x: String, y: String, z: String): Direction = {
    val xRepeats = x.contains("x")
    val yRepeats = y.contains("x")
    val zRepeats = z.contains("x")

    if (!(xRepeats || yRepeats || zRepeats))
      Direction(Array(Point3D(x.toInt, y.toInt, z.toInt)))
    else {

      val xClean = x.replace("x", "")
      val yClean = y.replace("x", "")
      val zClean = z.replace("x", "")

      val xMult = if (xClean.length > 0) xClean.toInt else 1
      val yMult = if (yClean.length > 0) yClean.toInt else 1
      val zMult = if (zClean.length > 0) zClean.toInt else 1

      // continuous movement jumps allowed (i.e. knight, bishop, rock, queen)
      var moves = Seq.empty[Point3D]
      for (x <- 0 until boardSize) {
        implicit def bool2int(b: Boolean): Int = if (b) 1 else 0

        moves = moves :+ Point3D(
          xMult + xRepeats * xMult * x,
          yMult + yRepeats * yMult * x,
          zMult + zRepeats * zMult * x)
      }
      Direction(moves.toArray)
    }
  }


  /**
   * get moveset corresponding to pieceType, empty moveset if none
   *
   * @param piece piece for which we want to know the moveset
   * @return moveset of the piece
   */
  def getMoveset(piece: Piece): Array[Direction] = movementMap.getOrElse(piece.pieceType, Array.empty[Direction])
}
