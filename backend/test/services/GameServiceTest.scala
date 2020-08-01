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

package services


import game.components.Facing.Facing
import game.components._
import game.managers.MovementManager
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json}


class GameServiceTest extends PlaySpec {

  val players: Array[Player] = Array(Player(0, 0), Player(1, 1))
  val id = 0
  val movementFile: JsValue = Json.parse(
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
  val piecesPosition: List[String] = List("", "", "TEST_PIECE", "TEST_PIECE", "", "", "", "TEST_PIECE", "TEST_PIECE", "", "", "", "", "", "", "", "", "")

  "GameService newGame" should {
    "create a new game and return associated ID" in {
      val result = GameService.newGame(players, nrPlanes = 2, boardSize = 6, movementFile, piecesPosition, id)
      result mustBe 0
      val piecesInBoard = GameService.getPiecesInBoard(result)
      piecesInBoard.nonEmpty mustBe true
      piecesInBoard.get.length mustBe 16
    }

    "reject wrong piecesPosition" in {
      val piecesPosition: List[String] = List("")
      try {
        GameService.newGame(players, nrPlanes = 2, boardSize = 6, movementFile, piecesPosition, id)
        fail()
      }
      catch {
        case e: IllegalArgumentException => e.getMessage == "Invalid format for pieces position"
      }
    }

    "return None for wrong gameId" in {
      val piecesInBoard = GameService.getPiecesInBoard(1)
      piecesInBoard.nonEmpty mustBe false
    }
  }

  "GameService" should {
    "return the number of planes" in {
      val id = 1
      val result = GameService.newGame(players, nrPlanes = 2, boardSize = 6, movementFile, piecesPosition, id)
      result mustBe id
      GameService.getNrPlanes(result) mustBe Some(2)
    }

    "return the boardSize" in {
      val id = 2
      val result = GameService.newGame(players, nrPlanes = 2, boardSize = 6, movementFile, piecesPosition, id)
      result mustBe id
      GameService.getBoardSize(result) mustBe Some(6)
    }

    "delete Game" in {
      GameService.remove(2)
      GameService.getBoardSize(2) mustBe None
      GameService.getNrPlanes(2) mustBe None
      GameService.getPiecesInBoard(2) mustBe None
    }
  }
}
