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
import game.managers.MovementManager
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json}

class MovementManagerTest extends PlaySpec {
  //,(x,x,0),(2x,2x,0),(0,1,z)
  val player: Player = Player(0, 0, "p1")
  val piece: Piece = Piece(player = player, pieceType = Type("TEST_PIECE"), position = Point3D(5, 5, 0), Facing.DOWN)

  "movementManager" should {
    "parse correct format" in {
      val jsonString: JsValue = Json.parse(
        """{
          "pieces": [
            {
              "name": "TEST_PIECE",
              "image": "nice image",
              "moveset": "(0,1,0),(1,-2x,0)"
            }
          ]
         }"""
      )
      val movementManager: MovementManager = MovementManager(8, 8, jsonString)
      val direction1: Direction = movementManager.getMoveset(piece)(0)
      val direction2: Direction = movementManager.getMoveset(piece)(1)

      direction1.dir.length mustBe 1
      direction1.dir(0) mustBe Point3D(0, 1, 0)

      direction2.dir.length mustBe 8
      for (i <- 1 until 8)
        direction2.dir(i - 1) mustBe Point3D(1, -2 * i, 0)
    }

    "invalid json fails" in {
      val jsonString: JsValue = Json.parse(
        """{
          "pieces": [
            {
              "name": "TEST_PIECE",
              "image": "nice image"
            }
          ]
         }"""
      )
      try {
        val movementManager: MovementManager = MovementManager(8, 8, jsonString)
        fail()
      }
      catch {
        case exception: Exception =>
      }
    }
  }

}
