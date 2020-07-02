package game

import game.components.Facing.Facing
import game.components._
import game.managers.{BoardConfigurator, MovementManager}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json}

class BoardConfiguratorTest extends PlaySpec {

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
  val piecesPosition: String = """NA,NA,NA,TEST_PIECE,TEST_PIECE,NA,NA,NA"""
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
      val piecesPosition: String = """NA,NA,NA,NA,TEST_PIECE,TEST_PIECE,NA,NA,NA,NA"""
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
      val players = Array(Player(0, 0), Player(1, 1))
      val movementManager: MovementManager = MovementManager(nrPlanes = 2, boardSize = 6, jsonString)
      val boards: Boards = Boards(movementManager)
      val piecesPosition: String = """NA,NA,TEST_PIECE,TEST_PIECE,NA,NA,NA,TEST_PIECE,TEST_PIECE,NA,NA,NA"""
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

