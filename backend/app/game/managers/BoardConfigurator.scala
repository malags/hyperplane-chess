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

package game.managers

import game.components.{Boards, Facing, Piece, Player, Point3D, Type}

import scala.util.Random

case class BoardConfigurator(piecesPosition: List[String]) {

  /**
   * Verify that the piecesPosition String has valid format and content
   *
   * @param boards the boards which we want to initialise
   * @return whether the format of piecesPosition is correct for the given boards
   */
  def verify(boards: Boards): Boolean = {
    // half board round down
    val nrSpots: Int = boards.movementManager.boardSize * (boards.movementManager.boardSize / 2)

    val validTypes = boards.movementManager.movementMap.keySet
    var typesFound = Set.empty[Type]

    for (piece <- piecesPosition)
      if (piece != "")
        typesFound = typesFound + Type(piece)

    piecesPosition.length == nrSpots && validTypes.equals(typesFound)
  }

  def initBoards(boards: Boards, players: Array[Player]): Boolean = {
    val nrPlayers = players.length
    // even distribution of game positions is possible
    if (boards.movementManager.nrPlanes * 2 % nrPlayers != 0)
      false
    else {
      val boardsPerPlayer = boards.movementManager.nrPlanes * 2 / nrPlayers
      var playersToAssign = nTimes(boardsPerPlayer, players)
      for (boardId <- 0 until boards.movementManager.nrPlanes) {
        // accepted retries
        var tolerance = 10
        var firstPlayer: Player = null
        var secondPlayer: Player = null
        while (tolerance > 0) {
          playersToAssign = Random.shuffle(playersToAssign)
          firstPlayer = playersToAssign.head
          secondPlayer = playersToAssign(1)

          tolerance -= 1
          // match as opponent
          if (firstPlayer.groupId != secondPlayer.groupId) {
            setPlayer(boards, boardId, firstPlayer, Facing.UP)
            setPlayer(boards, boardId, secondPlayer, Facing.DOWN)
            playersToAssign = playersToAssign.drop(2)
            tolerance = -1
          }
        }
        // force match even if allies if not matched
        if (tolerance != -1) {
          setPlayer(boards, boardId, firstPlayer, Facing.UP)
          setPlayer(boards, boardId, secondPlayer, Facing.DOWN)
          playersToAssign = playersToAssign.drop(2)
        }
      }
      true
    }

  }

  /**
   *
   * @param boards  boards in which we want to set player and piece
   * @param boardId the id (z) of the board
   * @param player  the player being added to the board
   * @param facing  the direction the player is facing
   */
  private def setPlayer(boards: Boards, boardId: Int, player: Player, facing: Facing.Value): Unit = {
    val boardSize: Int = boards.movementManager.boardSize

    for (index <- piecesPosition.indices) {
      if (piecesPosition(index) != "") {
        val pieceType: Type = Type(piecesPosition(index))

        // size = 8:   7 -> (7,0), 12 -> (4,1)
        val x: Int = index % boardSize
        val y: Int = index / boardSize
        val position: Point3D = Point3D(
          if (facing == Facing.DOWN) x else boardSize - x - 1,
          if (facing == Facing.DOWN) y else boardSize - y - 1,
          boardId)

        boards.addPiece(
          Piece(
            player,
            pieceType,
            position,
            facing)
        )
      }
    }
  }

  /**
   * Append players to itself n times
   *
   * @param n       how many times to append
   * @param players list to append to itself
   * @return players appended to itself n times
   */
  private def nTimes(n: Int, players: Array[Player]) = {
    var result = players
    var times = n
    while (times > 1) {
      result = result ++ players
      times -= 1
    }
    result.toSeq
  }


}
