package services


import game.components.Facing.Facing
import game.components._
import game.managers.MovementManager
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json}


class GameServiceTest extends PlaySpec {

  val players: Array[Player] = Array(Player(0, 0), Player(1, 1))
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
  val piecesPosition: String = """NA,NA,TEST_PIECE,TEST_PIECE,NA,NA,NA,TEST_PIECE,TEST_PIECE,NA,NA,NA,NA,NA,NA,NA,NA,NA"""

  "GameService newGame" should {
    "create a new game and return associated ID" in {
      val result = GameService.newGame(players, nrPlanes = 2, boardSize = 6, movementFile, piecesPosition)
      result mustBe 0
      val piecesInBoard = GameService.getPiecesInBoard(result)
      piecesInBoard.nonEmpty mustBe true
      piecesInBoard.get.length mustBe 16
    }

    "reject wrong piecesPosition" in {
      val piecesPosition: String = """NA"""
      try {
        GameService.newGame(players, nrPlanes = 2, boardSize = 6, movementFile, piecesPosition)
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
}
