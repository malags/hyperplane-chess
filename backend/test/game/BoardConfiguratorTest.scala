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

import game.components.Facing.Facing
import game.components._
import game.managers.{BoardConfigurator, MovementManager}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json}

class BoardConfiguratorTest extends PlaySpec {

  val player: Player = Player(0, 0, "")
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
  val piecesPosition: List[String] = List("", "", "", "TEST_PIECE", "TEST_PIECE", "", "", "")
  val movementManager: MovementManager = MovementManager(nrPlanes = 2, boardSize = 4, jsonString)
  val boards: Boards = Boards(movementManager)

  "BoardConfigurator" should {
    "verify correct format even" in {
      val boardConfigurator: BoardConfigurator = BoardConfigurator(piecesPosition = piecesPosition)
      boardConfigurator.verify(boards = boards) mustBe true
    }

    "verify correct format odd" in {
      val movementManager: MovementManager = MovementManager(nrPlanes = 2, boardSize = 5, jsonString)
      val boards: Boards = Boards(movementManager)
      val piecesPosition: List[String] = List("", "", "", "", "TEST_PIECE", "TEST_PIECE", "", "", "", "")
      val boardConfigurator: BoardConfigurator = BoardConfigurator(piecesPosition = piecesPosition)

      boardConfigurator.verify(boards = boards) mustBe true
    }


    "not verify wrong format" in {
      val movementManager: MovementManager = MovementManager(nrPlanes = 2, boardSize = 6, jsonString)
      val boards: Boards = Boards(movementManager)
      val boardConfigurator: BoardConfigurator = BoardConfigurator(piecesPosition = piecesPosition)

      boardConfigurator.verify(boards = boards) mustBe false
    }

    "initialize the board" in {
      val players = Array(Player(0, 0, "p1"), Player(1, 1, "p2"))
      val movementManager: MovementManager = MovementManager(nrPlanes = 2, boardSize = 6, jsonString)
      val boards: Boards = Boards(movementManager)

      val piecesPosition: List[String] = List("", "", "TEST_PIECE", "TEST_PIECE", "", "", "", "TEST_PIECE", "TEST_PIECE", "", "", "", "", "", "", "", "", "")
      val boardConfigurator: BoardConfigurator = BoardConfigurator(piecesPosition = piecesPosition)
      boardConfigurator.initBoards(boards, players)
      /*
      Y
        +---+---+---+---+---+---+
      0 |   |   | T | T |   |   |
        +---+---+---+---+---+---+
      1 |   | T | T |   |   |   |
        +---+---+---+---+---+---+
      2 |   |   |   |   |   |   |
        +---+---+---+---+---+---+
      3 |   |   |   |   |   |   |
        +---+---+---+---+---+---+
      4 |   |   |   | T | T |   |
        +---+---+---+---+---+---+
      5 |   |   | T | T |   |   |
        +-----------------------+
        | 0 | 1 | 2 | 3 | 4 | 5 | X
       */

      boards.pieceAt(Point3D(2, 5, 0)).nonEmpty mustBe true
      boards.pieceAt(Point3D(3, 5, 0)).nonEmpty mustBe true
      boards.pieceAt(Point3D(3, 4, 0)).nonEmpty mustBe true
      boards.pieceAt(Point3D(4, 4, 0)).nonEmpty mustBe true

      // check different plane
      boards.pieceAt(Point3D(1, 1, 1)).nonEmpty mustBe true
      boards.pieceAt(Point3D(2, 1, 1)).nonEmpty mustBe true
      boards.pieceAt(Point3D(2, 0, 1)).nonEmpty mustBe true
      boards.pieceAt(Point3D(3, 0, 1)).nonEmpty mustBe true

      boards.pieceAt(Point3D(3, 0, 0)).get.facing mustBe Facing.DOWN
      boards.pieceAt(Point3D(3, 5, 0)).get.facing mustBe Facing.UP
    }

  }


}

