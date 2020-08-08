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

import {combineReducers} from "redux";
import {
    CHAT_SET_NAME,
    CHAT_SEND,
    CHAT_GOT,
    SET_CONNECTION,
    SET_GAME_ID,
    SET_PLAYER,
    NEW_PLAYER,
    GET_READY_STATUS
} from "./actions";

const initialState = require("./initialState.json")


const gotMessage = (state, action) => {
    let tempMessages = [...state.messages, action.message]
    return {
        ...state,
        messages: tempMessages.length >= state.break_size ?
            tempMessages.slice(state.break_size / 2) :
            tempMessages
    }
}

const setPlayer = (state, action) => {
    let player = action.player
    let players = state.players.filter(p => p.playerId != action.player.playerId)
    players.push(action.player)
    // changed player is user
    if (player.playerId == state.player.playerId) {
        return {
            ...state,
            player: action.player,
            players: players
        }
    }
    // changed player is another user
    else {
        return {
            ...state,
            players: players
        }
    }
}


function reducer(state = initialState, action) {
    switch (action.type) {
        case CHAT_GOT:
            return gotMessage(state, action)
        case CHAT_SEND:
            return {...state}
        case CHAT_SET_NAME:
            return {
                ...state,
                player: {
                    ...state.player,
                    name: action.name,
                }
            }
        case SET_PLAYER:
            return setPlayer(state, action)
        case SET_CONNECTION:
            return {
                ...state,
                connection: action.connection
            }
        case SET_GAME_ID:
            return {
                ...state,
                player: {
                    ...state.player,
                    gameId: action.gameId,
                }
            }
        case GET_READY_STATUS:
            return {
                ...state,
                playersReady: action.playersReady
            }
        case NEW_PLAYER:
            return {
                ...state,
                player: action.player
            }
        default:
            return {...state}
    }
}

const rootReducer = reducer

export default rootReducer
