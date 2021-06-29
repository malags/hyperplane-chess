/*
 * Copyright © 2021 Stefano Malagò
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package utils

import game.components.{Facing, Piece, Player, Point3D, Type}
import play.api.libs.json.{Json, Reads, Writes}

/**
 * Object containing Readers and Writers for the Game classes to allow conversion between Json and the specific class
 * use import utils.JsonUtils._
 */
object JsonUtils {
  implicit val playerReads: Reads[Player] = Json.reads[Player]
  implicit val playerWrites: Writes[Player] = Json.writes[Player]

  implicit val point3DReads: Reads[Point3D] = Json.reads[Point3D]
  implicit val point3DWrites: Writes[Point3D] = Json.writes[Point3D]

  implicit val typeReads: Reads[Type] = Json.reads[Type]
  implicit val typeWrites: Writes[Type] = Json.writes[Type]

  implicit val facingReads: Reads[Facing.Value] = Reads.enumNameReads(Facing)
  implicit val facingWrites: Writes[Facing.Value] = Writes.enumNameWrites

  implicit val pieceReads: Reads[Piece] = Json.reads[Piece]
  implicit val pieceWrites: Writes[Piece] = Json.writes[Piece]
}
