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

package game

import game.components._
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json}

class GameTest extends PlaySpec {

  val player: Player = Player(0, 0)
  val opponent: Player = Player(1, 1)
  val type_t: Type = Type("TEST_PIECE")

  val myPiecePos: Point3D = Point3D(5, 5, 0)
  val enemyPiecePos: Point3D = Point3D(5, 6, 0)
  val myPiece: Piece = Piece(player, type_t, myPiecePos, Facing.DOWN)
  val enemyPiece: Piece = Piece(opponent, type_t, enemyPiecePos, Facing.UP)
  val movementFile: JsValue = Json.parse(
    """{
          "pieces": [
            {
              "name": "TEST_PIECE",
              "image": "nice image",
              "moveset": "(0,1,0)"
            }
          ]
         }"""
  )


  val game: Game = new Game(players = Array(player, opponent), nrPlanes = 2, boardSize = 8, movementFile = movementFile)
  game.boards.addPiece(myPiece)
  game.boards.addPiece(enemyPiece)

  "Game endTurn" should {
    "increases" in {
      game.turnId mustBe 0
      game.endTurn()
      game.turnId mustBe 1
    }

    "loops" in {
      game.turnId mustBe 1
      game.endTurn()
      game.turnId mustBe 0
    }
  }

  "Game pieceAt" should {
    "return Piece if present" in {
      game.pieceAt(myPiecePos).get mustBe myPiece
    }

    "return None if not present" in {
      game.pieceAt(Point3D(0, 0, 0)).isEmpty mustBe true
    }
  }

  "Game movesAt" should {
    val game: Game = new Game(players = Array(player, opponent), nrPlanes = 2, boardSize = 8, movementFile = movementFile)
    game.boards.addPiece(myPiece)
    game.boards.addPiece(enemyPiece)

    "return empty if not turn of player associated with piece" in {
      game.movesAt(enemyPiecePos) mustBe empty
    }


    "return moves if turn of player associated with piece" in {
      game.movesAt(myPiecePos) mustBe Set(enemyPiecePos)
    }

    "return empty if no piece is in position" in {
      game.movesAt(Point3D(0, 0, 0)) mustBe empty
    }
  }

}
