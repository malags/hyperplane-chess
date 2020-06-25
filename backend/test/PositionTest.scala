import game.components.{Boards, Point3D}
import org.scalatestplus.play.PlaySpec


class PositionTest extends PlaySpec {

  val boards: Boards = Boards(nrPlanes = 2, boardSize = 8)
  val start: Point3D = Point3D(1, 2, 0)

  "sum of two points" should {
    "two positive points without loop" in {

      val dir = Point3D(1, 2, 1)
      val result = start.add(dir, boards)

      result.x mustBe 2
      result.y mustBe 4
      result.z mustBe 1
    }

    "negative point without loop" in {

      val dir = Point3D(1, -1, 0)
      val result = start.add(dir, boards)

      result.x mustBe 2
      result.y mustBe 1
      result.z mustBe 0
    }

    "two positive points with loop" in {

      val dir = Point3D(0, 0, 2)
      val result = start.add(dir, boards)

      result.x mustBe 1
      result.y mustBe 2
      result.z mustBe 0
    }

    "two positive points with double loop" in {

      val dir = Point3D(0, 0, 4)
      val result = start.add(dir, boards)

      result.x mustBe 1
      result.y mustBe 2
      result.z mustBe 0
    }

    "two positive points with half loop" in {

      val dir = Point3D(0, 0, 3)
      val result = start.add(dir, boards)

      result.x mustBe 1
      result.y mustBe 2
      result.z mustBe 1
    }

    "negative point with loop" in {

      val dir = Point3D(0, 0, -1)
      val result = start.add(dir, boards)

      result.x mustBe 1
      result.y mustBe 2
      result.z mustBe 1
    }

    "negative point with double loop" in {

      val dir = Point3D(0, 0, -3)
      val result = start.add(dir, boards)

      result.x mustBe 1
      result.y mustBe 2
      result.z mustBe 1
    }
  }

  "a Point3D" should {
    "outOfBounds when position.value is negative" in {

      Point3D(-1, 0, 0).isOutOfBounds(boards) mustBe true
      Point3D(0, -1, 0).isOutOfBounds(boards) mustBe true
      Point3D(0, 0, -1).isOutOfBounds(boards) mustBe true
    }

    "outOfBounds when position.value is over board max value" in {

      Point3D(8, 0, 0).isOutOfBounds(boards) mustBe true
      Point3D(0, 8, 0).isOutOfBounds(boards) mustBe true
      Point3D(0, 0, 2).isOutOfBounds(boards) mustBe true
    }

    "not outOfBounds when position.value is valid" in {

      Point3D(0, 0, 0).isOutOfBounds(boards) mustBe false
      Point3D(0, 7, 0).isOutOfBounds(boards) mustBe false
      Point3D(0, 0, 1).isOutOfBounds(boards) mustBe false
    }
  }

}
