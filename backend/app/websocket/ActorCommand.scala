/*
 * Copyright © 2021 Stefano Malagò
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package websocket

object ActorCommand extends Enumeration {
  type ActorCommand = Value
  val PING, MESSAGE, GET_AVAILABLE_MOVES, SUBMIT_MOVE, GET_GAME_STATUS, READY, SET_PLAYER, NEW_PLAYER,
  GET_ALL_PLAYERS, GET_ALL_READY_STATUS, UNKNOWN = Value

  def toCommand(command: String): ActorCommand = {
    command match {
      case "ping" => PING
      case "message" => MESSAGE
      case "getAvailableMoves" => GET_AVAILABLE_MOVES
      case "submitMove" => SUBMIT_MOVE
      case "getGameStatus" => GET_GAME_STATUS
      case "ready" => READY
      case "setPlayer" => SET_PLAYER
      case "newPlayer" => NEW_PLAYER
      case "getAllPlayers" => GET_ALL_PLAYERS
      case "getAllReadyStatus" => GET_ALL_READY_STATUS
      case _ => UNKNOWN
    }
  }
}

