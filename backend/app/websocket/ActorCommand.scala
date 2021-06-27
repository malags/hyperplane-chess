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

