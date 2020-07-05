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

import React, {Component} from "react";
import ReactDOM from "react-dom";
import Connection from "../classes/Connection"
import * as PIXI from 'pixi.js'

class Game extends Component {
    constructor(props) {
        super(props);
        let connection = new Connection("ws://localhost:9000/socket?id=1")//new WebSocket(this.props.socket_url) //TODO change back
        this.state = {
            connection: connection,
            tile_size: 30,
            selectedPos: null,
            player: {
                playerId: 0, groupId: 0
            }
        }
    }

    componentDidMount() {
        let type = "WebGL"
        if (!PIXI.utils.isWebGLSupported()) {
            type = "canvas"
        }
        PIXI.utils.sayHello(type)


        let pixi = new PIXI.Application({
            width: 900,
            height: 900,
            backgroundColor: 0x666666
        })


        //background empty image to handle clicks outside board
        let bkg = new PIXI.Sprite(PIXI.Texture.EMPTY);
        bkg.position.x = 0;
        bkg.position.y = 0;
        bkg.width = pixi.screen.width;
        bkg.height = pixi.screen.height;
        bkg.interactive = true
        bkg.on("mousedown", () => this.setState({selectedPos: null}))
        pixi.stage.addChild(bkg);

        this.GView.appendChild(pixi.view)

        this.setState({pixi: pixi})
    }

    something = () => {
        this.setState({add: "something"})
        this.state.connection.sendGetGameStatus()

        this.drawNBoards(3, 9)
    };

    drawNBoards(n, size) {
        let center = this.state.pixi.screen.width / 2
        let radius = 1.3 * center / 2 // TODO: needs tweaking boards may overlap, consider list of values for size/n
        let dir = [0, -radius]

        for (let part = 0; part < n; ++part) {
            let v = this._rotateVector(dir, part * 360 / n)
            let pos_x = center + v[0] - this.state.tile_size * size / 2
            let pos_y = center + v[1] - this.state.tile_size * size / 2
            this._drawBoard(pos_x, pos_y, size, part)
        }
    }

    _rotateVector(vec, ang) {
        ang = -ang * (Math.PI / 180);
        let cos = Math.cos(ang);
        let sin = Math.sin(ang);
        return [Math.round(10000 * (vec[0] * cos - vec[1] * sin)) / 10000,
            Math.round(10000 * (vec[0] * sin + vec[1] * cos)) / 10000]
    };

    _drawBoard(pos_x, pos_y, size, z) {

        let tile_size = this.state.tile_size

        for (let x = 0; x < size; ++x) {
            for (let y = 0; y < size; ++y) {
                this._addSquareAt(
                    pos_x + tile_size * x,
                    pos_y + tile_size * y,
                    {x: x, y: y, z: z}
                )
            }
        }
    }

    _addSquareAt(x, y, p) {
        let square = new PIXI.Sprite(PIXI.Texture.WHITE);

        square.tint = (p.x + p.y + p.z) % 2 === 0 ? 0xffffff : 0x000000
        square.width = this.state.tile_size;
        square.height = this.state.tile_size;

        square.x = x
        square.y = y

        const point = p

        let click = () => {
            if (this.state.selectedPos != null) {
                // submitMove
                console.log("send move")
                this.state.connection.sendMove(this.state.selectedPos, point, this.state.player)
                this.setState({selectedPos: null})
            } else {
                console.log("selected")
                // request available moves
                this.setState({selectedPos: point})
                this.state.connection.sendAvailMovesRequest(point, this.state.player)
                console.log(point)
            }
        }

        square.interactive = true
        square.on('touchstart', click);
        square.on("mousedown", click)
        this.state.pixi.stage.addChild(square)
    }

    render() {
        return (
            <div className="Game">
                <p>Game</p>
                <button onClick={this.something}>Websocket request</button>
                <div ref={(el) => {
                    this.GView = el
                }}/>
            </div>
        );
    }
}

export default Game;
