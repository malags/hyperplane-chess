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

import store from "../redux/Store"
import {
    getAllReadyStatusAction,
    gotMessageAction, newPlayerAction,
    setPlayerAction
} from "../redux/actions";
import React from "react";

/**
 * Connection to the server with websocket
 */
class Connection {

    constructor(socket_url) {
        this.socket = new WebSocket(socket_url)
        this.socket.onopen = () => {
            console.log("connected")
            this.sendGetGameStatus()
        }
        this.socket.onerror = () => console.log("websocket error")
        this.socket.onclose = (message) => {
            console.log(message.reason)
            console.log("disconnected")
        }
        this.socket.onmessage = (message) => this.messageHandler(message)

        this.keepAliveInterval = setInterval(function () {
            if (this.socket.readyState === WebSocket.OPEN) {
                console.log("ping")
                this.socket.send(JSON.stringify({command: "ping"}))
            }
        }.bind(this), 50000);
    }

    setGame(game) {
        this.game = game
    }


    messageHandler(message) {
        let json = JSON.parse(message.data)
        // submitMove / availableMoves / gameStatus
        if (json.status === "ok") {
            let command = json.command
            this.gameHandler(command, json)
            this.chatHandler(command, json)
            this.gameConfigHandler(command, json)
        }
    }

    gameHandler(command, json) {
        switch (command) {
            case "submitMove": // received move done
                console.log("TODO Connection/gameHandler")
                console.log(json) //TODO actual action
                break
            case "availableMoves":  // requested moves for some position
                this.game.updateAvailable(json)
                break
            case "gameStatus":
                this.game.updateGame(json)
                break
            default:
        }
    }

    chatHandler(command, json) {
        switch (command) {
            case "message":
                store.dispatch(gotMessageAction(json.data))
            default:
        }
    }

    gameConfigHandler(command, json) {
        switch (command) {
            case "setPlayer":
                store.dispatch(setPlayerAction(json.data))
                break
            case "newPlayer":
                store.dispatch(newPlayerAction(json.data))
                store.dispatch(setPlayerAction(json.data))
                break
            case "getAllReadyStatus":
                store.dispatch(getAllReadyStatusAction(json.data))
                break
            case "gameStart":
                window.location.pathname = "game"
                break
            default:

        }
    }

    /**
     * get available moves for piece at position
     * @param pos position as json {x:1 , y:1, z:1}
     * @param player the player
     */
    sendAvailMovesRequest(pos, player) {
        this._send(
            {
                command: "getAvailableMoves",
                data: {
                    from: pos,
                    player: player
                }
            }
        )
    }

    sendMove(src, target, player) {
        this._send(
            {
                command: "submitMove",
                data: {
                    from: src,
                    to: target,
                    player: player
                }
            }
        )
    }

    sendGetGameStatus() {
        this._send({command: "getGameStatus"})
    }

    sendMessage(message) {
        this._send({command: "message", data: message})
    }

    sendSetReady(player, isReady) {
        this._send({command: "ready", data: {player, ready: isReady}})
    }

    sendNewPlayerRequest() {
        this._send({command: "newPlayer"})
    }

    sendSetPlayer(player) {
        this._send({command: "setPlayer", data: player})
    }

    sendGetAllPlayers() {
        this._send({command: "getAllPlayers"})
    }

    sendGetAllReadyStatus() {
        this._send({command: "getAllReadyStatus"})
    }

    _send(message) {
        this.socket.send(JSON.stringify(message))
    }

    close() {
        this.socket.close()
        clearInterval(this.keepAliveInterval)
    }
}


export default Connection
