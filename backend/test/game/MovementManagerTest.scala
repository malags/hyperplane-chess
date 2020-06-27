package game

import game.components._
import game.managers.MovementManager
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json}

class MovementManagerTest extends PlaySpec {
  //,(x,x,0),(2x,2x,0),(0,1,z)
  val player: Player = Player(0, 0)
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
