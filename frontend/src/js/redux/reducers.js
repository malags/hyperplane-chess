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
import {CHAT_SET_NAME, CHAT_SEND, CHAT_GOT} from "./actions";

const initialState = require("./initialState.json")

function setChatName(state = initialState, action) {
    console.log(action)
    if (action.type === CHAT_SET_NAME) {
        return {
            ...state,
            name: action.name
        }
    }
    return state
}

function messageReducer(state = initialState, action) {
    switch (action.type) {
        case CHAT_GOT:
            return gotMessage(state, action)
        case CHAT_SEND:
            return state

    }
    return state
}


const gotMessage = (state, action) => {
    let tempMessages = [...state.messages, action.message]
    return {
        ...state,
        messages: tempMessages.length >= state.break_size ?
            tempMessages.slice(state.break_size / 2) :
            tempMessages
    }
}

// const rootReducer = combineReducers({
//     name: setChatName,
//     messages: messageReducer
// })

function reducer(state = initialState, action) {
    console.log(action)
    switch (action.type) {
        case CHAT_SET_NAME:
            return {
                ...state,
                name: action.name
            }
        case CHAT_GOT:
            return gotMessage(state, action)
        case CHAT_SEND:
            return {...state}
        default:
            return {...state}
    }
}

const rootReducer = reducer

export default rootReducer
