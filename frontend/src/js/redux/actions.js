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


/*
 * action types
 */
export const CHAT_SET_NAME = "CHAT_SET_NAME"
export const SET_PLAYER_ID = "SET_PLAYER_ID"
export const SET_GROUP_ID = "SET_GROUP_ID"
export const CHAT_GOT = "CHAT_GOT"
export const CHAT_SEND = "CHAT_SEND"
export const SET_CONNECTION = "SET_CONNECTION"
export const SET_GAME_ID = "SET_GAME_ID"
export const SET_PLAYERS_PER_GROUP = "SET_PLAYERS_PER_GROUP"
export const PLAYER_READY = "PLAYER_READY"


/*
 * action creators
 */

export function setNameAction(name) {
    return {type: CHAT_SET_NAME, name}

}

export function setPlayerIdAction(playerId) {
    return {type: SET_PLAYER_ID, playerId}
}

export function setGroupIdAction(groupId) {
    return {type: SET_GROUP_ID, groupId}
}

export function setConnectionAction(connection) {
    return {type: SET_CONNECTION, connection}

}

export function setGameIdAction(gameId) {
    return {type: SET_GAME_ID, gameId}
}

export function gotMessageAction(message) {
    return {type: CHAT_GOT, message}
}

export function sendMessageAction(message) {
    return {type: CHAT_SEND, message}
}

export function setPlayersPerGroupAction(playersPerGroup) {
    return {type: SET_PLAYERS_PER_GROUP, playersPerGroup}
}

export function playerReadyAction(readyState) {
    return {type: PLAYER_READY, readyState}
}
