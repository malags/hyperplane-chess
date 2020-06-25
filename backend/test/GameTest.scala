import game.components.{Game, Piece, Player, Point3D, Type}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json}

class GameTest extends PlaySpec {

  val player: Player = Player(0, 0)
  val opponent: Player = Player(1, 1)
  val type_t: Type = Type("TEST_PIECE")

  val myPiecePos: Point3D = Point3D(5, 5, 0)
  val enemyPiecePos: Point3D = Point3D(5, 6, 0)
  val myPiece: Piece = Piece(player, type_t, myPiecePos)
  val enemyPiece: Piece = Piece(opponent, type_t, enemyPiecePos)
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
