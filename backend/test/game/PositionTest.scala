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

import game.components.{Boards, Facing, Point3D}
import game.managers.MovementManager
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json


class PositionTest extends PlaySpec {

  val movementManager: MovementManager = MovementManager(nrPlanes = 2, boardSize = 8, movementJson =
    Json.parse(
      """
        |{
        |          "pieces": [
        |            {
        |              "name": "TEST_PIECE",
        |              "image": "nice image",
        |              "moveset": "(0,1,0)"
        |            }
        |          ]
        |         }
        |""".stripMargin))
  val boards: Boards = Boards(movementManager = movementManager)
  val start: Point3D = Point3D(1, 2, 0)

  "sum of two points" should {
    "two positive points without loop" in {

      val dir = Point3D(1, 2, 1)
      val result = start.add(dir, movementManager)

      result.x mustBe 2
      result.y mustBe 4
      result.z mustBe 1
    }

    "negative point without loop" in {

      val dir = Point3D(1, -1, 0)
      val result = start.add(dir, movementManager)

      result.x mustBe 2
      result.y mustBe 1
      result.z mustBe 0
    }

    "two positive points with loop" in {

      val dir = Point3D(0, 0, 2)
      val result = start.add(dir, movementManager)

      result.x mustBe 1
      result.y mustBe 2
      result.z mustBe 0
    }

    "two positive points with double loop" in {

      val dir = Point3D(0, 0, 4)
      val result = start.add(dir, movementManager)

      result.x mustBe 1
      result.y mustBe 2
      result.z mustBe 0
    }

    "two positive points with half loop" in {

      val dir = Point3D(0, 0, 3)
      val result = start.add(dir, movementManager)

      result.x mustBe 1
      result.y mustBe 2
      result.z mustBe 1
    }

    "negative point with loop" in {

      val dir = Point3D(0, 0, -1)
      val result = start.add(dir, movementManager)

      result.x mustBe 1
      result.y mustBe 2
      result.z mustBe 1
    }

    "negative point with double loop" in {

      val dir = Point3D(0, 0, -3)
      val result = start.add(dir, movementManager)

      result.x mustBe 1
      result.y mustBe 2
      result.z mustBe 1
    }
  }

  "a Point3D" should {

    "convert to absolute reference system" in {
      Point3D(1, 1, 1).toAbsolute(Facing.UP) mustBe Point3D(-1, -1, 1)
      Point3D(1, 1, 1).toAbsolute(Facing.DOWN) mustBe Point3D(1, 1, 1)
    }

    "outOfBounds when position.value is negative" in {

      Point3D(-1, 0, 0).isOutOfBounds(movementManager) mustBe true
      Point3D(0, -1, 0).isOutOfBounds(movementManager) mustBe true
      Point3D(0, 0, -1).isOutOfBounds(movementManager) mustBe true
    }

    "outOfBounds when position.value is over board max value" in {

      Point3D(8, 0, 0).isOutOfBounds(movementManager) mustBe true
      Point3D(0, 8, 0).isOutOfBounds(movementManager) mustBe true
      Point3D(0, 0, 2).isOutOfBounds(movementManager) mustBe true
    }

    "not outOfBounds when position.value is valid" in {

      Point3D(0, 0, 0).isOutOfBounds(movementManager) mustBe false
      Point3D(0, 7, 0).isOutOfBounds(movementManager) mustBe false
      Point3D(0, 0, 1).isOutOfBounds(movementManager) mustBe false
    }
  }

}
