import game.components.{Boards, Facing, Game, Piece, Player, Point3D, Type}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json}

class PieceTest extends PlaySpec {

  val player: Player = Player(0, 0)
  val opponent: Player = Player(1, 1)
  val type_t: Type = Type("TEST_PIECE")

  val myPiece: Piece = Piece(player, type_t, Point3D(5, 5, 0), Facing.DOWN)
  val enemyPiece: Piece = Piece(opponent, type_t, Point3D(5, 6, 0), Facing.UP)

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

  "Piece enemy status" should {
    "enemy with different group" in {
      myPiece.isEnemy(opponent) mustBe true
    }

    "ally with same group" in {
      myPiece.isEnemy(player) mustBe false
    }
  }

  "Piece available moves" should {
    "available when tile is free" in {
      val piece: Piece = Piece(player, type_t, Point3D(4, 4, 1), Facing.DOWN)
      piece.availableMoves(game) mustBe Set(Point3D(4, 5, 1))
    }

    "available when tile is free and facing UP" in {
      val piece: Piece = Piece(player, type_t, Point3D(4, 4, 1), Facing.UP)
      piece.availableMoves(game) mustBe Set(Point3D(4, 3, 1))
    }

    "available when tile has enemy" in {
      myPiece.availableMoves(game) mustBe Set(Point3D(5, 6, 0))
    }

    "occupied when tile has ally" in {
      val piece: Piece = Piece(player, type_t, Point3D(5, 4, 0), Facing.DOWN)
      piece.availableMoves(game) mustBe Set.empty[Point3D]
    }
  }


}
