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

import game.components.Facing.Facing
import game.components.{Boards, Facing, Piece, Player, Point3D, Type}
import game.managers.MovementManager
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json}


class BoardsTest extends PlaySpec {

  val player: Player = Player(0, 0)
  val type_t: Type = Type("")
  val facing: Facing = Facing.DOWN

  val jsonString: JsValue = Json.parse(
    """{
          "pieces": [
            {
              "name": "TEST_PIECE",
              "image": "nice image",
              "moveset": "(0,1,0),(1,1,0)"
            }
          ]
         }"""
  )
  val movementManager: MovementManager = MovementManager(nrPlanes = 2, boardSize = 8, jsonString)


  "new board" should {
    "be empty" in {
      val boards = Boards(movementManager)
      for (x <- 0 to 8)
        for (y <- 0 to 8)
          for (z <- 0 to 2)
            boards.pieceAt(Point3D(x, y, z)).isEmpty mustBe true
    }
  }

  "new board then add piece" should {
    "contain piece" in {
      val boards = Boards(movementManager)

      val position = Point3D(x = 1, y = 2, z = 1)
      val piece = Piece(player, type_t, position, facing)
      boards.addPiece(piece)

      boards.pieceAt(position).isEmpty mustBe false
      boards.pieceAt(position).get mustBe piece
    }
  }

  "new board then add piece then move" should {
    "move piece" in {
      val boards = Boards(movementManager)

      val startPosition = Point3D(x = 1, y = 2, z = 1)
      val endPosition = Point3D(x = 1, y = 2, z = 0)
      val piece = Piece(player, type_t, startPosition, facing)


      boards.addPiece(piece)

      // start position
      boards.pieceAt(startPosition).isEmpty mustBe false
      boards.pieceAt(startPosition).get mustBe piece

      boards.movePiece(startPosition, endPosition)

      // moved piece not in starting position
      boards.pieceAt(startPosition).isEmpty mustBe true
      // end position
      boards.pieceAt(endPosition).isEmpty mustBe false
      boards.pieceAt(endPosition).get mustBe Piece(player, type_t, endPosition, facing)

    }
  }
}
