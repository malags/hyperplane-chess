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
import Row from "react-bootstrap/Row";
import Container from "react-bootstrap/Container";

class Game extends Component {
    constructor(props) {
        super(props);
        let connection = new Connection("ws://localhost:9000/socket?id=1", this)//new WebSocket(this.props.socket_url) //TODO change back
        this.state = {
            connection: connection,
            tile_size: 30,
            selectedPos: null,
            player: {
                playerId: 0, groupId: 0
            },
            nrBoards: 0,
            boardSize: 6,
            pieces: null
        }
    }

    componentDidMount() {
        let type = "WebGL"
        if (!PIXI.utils.isWebGLSupported()) {
            type = "canvas"
        }
        PIXI.utils.sayHello(type)


        let pixi = new PIXI.Application({
            resizeTo: window,
            backgroundColor: 0x666666
        })

        //background empty image to handle clicks outside board
        let container = new PIXI.Container();
        let bkg = new PIXI.Sprite(PIXI.Texture.EMPTY);
        bkg.position.x = 0;
        bkg.position.y = 0;
        bkg.width = pixi.screen.width;
        bkg.height = pixi.screen.height;
        bkg.interactive = true
        bkg.on("mousedown", () => {
            this.setState({selectedPos: null})
            this.updateAvailable()
        })

        container.addChild(bkg)

        let movesContainer = new PIXI.Container();
        let spriteContainer = new PIXI.Container();
        pixi.stage.addChild(container);
        pixi.stage.addChild(movesContainer)
        pixi.stage.addChild(spriteContainer);


        this.GView.appendChild(pixi.view)

        this.setState({
            pixi: pixi
        })
    }

    drawNBoards() {
        let n = this.state.nrBoards
        let boardSize = this.state.boardSize
        let tile_size = this.state.tile_size
        let center = Math.min(this.state.pixi.screen.width, this.state.pixi.screen.height) / 2
        let radius = 1.3 * center / 2 // TODO: needs tweaking boards may overlap, consider list of values for size/n
        let dir = [0, -radius]

        for (let part = 0; part < n; ++part) {
            let v = this._rotateVector(dir, part * 360 / n)
            let pos_x = (this.state.pixi.screen.width / 2) + v[0] - tile_size * boardSize / 2
            let pos_y = (this.state.pixi.screen.height / 2) + v[1] - tile_size * boardSize / 2
            this._drawBoard(pos_x, pos_y, part)
        }
    }

    _rotateVector(vec, ang) {
        ang = -ang * (Math.PI / 180);
        let cos = Math.cos(ang);
        let sin = Math.sin(ang);
        return [Math.round(10000 * (vec[0] * cos - vec[1] * sin)) / 10000,
            Math.round(10000 * (vec[0] * sin + vec[1] * cos)) / 10000]
    };

    _drawBoard(pos_x, pos_y, z) {
        let tile_size = this.state.tile_size
        let size = this.state.boardSize
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

    /**
     * Redraw the game with current saved state
     * @private
     */
    _redrawGame() {
        console.log("redraw")
        this.drawNBoards()
        this.state.pixi.stage.children[1].removeChildren()
        for (let piece in this.state.pieces) {
            this._drawPiece(this.state.pieces[piece])
        }
    }


    /**
     * Draw the given piece at the correct position
     * @param piece piece to be drawn
     * @private
     */
    _drawPiece(piece) {
        let [x, y] = this._pointToCoord(piece.position)

        // TODO use real texture
        let circle = new PIXI.Graphics();

        let color = piece.player.playerId == 0 ? 0xFF0000 : 0x00FF00
        circle.lineStyle(1, color, 1);
        circle.beginFill(color, 1);

        circle.drawCircle(x + 15, y + 15, 10);
        let texture = this.state.pixi.renderer.generateTexture(circle);
        //////////////////////

        let sprite = new PIXI.Sprite(texture);
        sprite.x = x + 5
        sprite.y = y + 5
        this.state.pixi.stage.children[2].addChild(sprite)

    }


    /**
     * Converts a Point3D to it's coordinate in PIXI
     * @param point point3D to convert to PIXI coord
     * @returns {coordinates [x, y]}
     * @private
     */
    _pointToCoord(point) {
        let nrBoards = this.state.nrBoards
        let boardSize = this.state.boardSize
        let tile_size = this.state.tile_size
        let center = Math.min(this.state.pixi.screen.width, this.state.pixi.screen.height) / 2
        let radius = 1.3 * center / 2

        let dir = [0, -radius]
        let v = this._rotateVector(dir, point.z * 360 / nrBoards)

        let pos_x = (this.state.pixi.screen.width / 2) + v[0] - tile_size * boardSize / 2
        let pos_y = (this.state.pixi.screen.height / 2) + v[1] - tile_size * boardSize / 2

        let x = pos_x + tile_size * point.x
        let y = pos_y + tile_size * point.y

        return [x, y]
    }


    updateGame(json) {
        console.log("update Pieces")
        this.setState({
                pieces: json.data.pieces,
                nrBoards: json.data.nrPlanes,
                boardSize: json.data.boardSize
            }
        )
        console.log(json)
        this.updateAvailable()
    }

    updateAvailable(json) {
        this.state.pixi.stage.children[2].removeChildren()
        if (json)
            this._addAvailableMoves(json.data)
        this._redrawGame()
    }


    _addAvailableMoves(moves) {

        for (let move in moves) {
            let pos = moves[move]

            let square = new PIXI.Sprite(PIXI.Texture.WHITE);

            square.tint = 0x00FF00
            square.alpha = 0.8
            square.width = this.state.tile_size;
            square.height = this.state.tile_size;

            let [x, y] = this._pointToCoord(pos)
            square.x = x
            square.y = y
            this.state.pixi.stage.children[2].addChild(square)
        }

    }

    /**
     * add a background square to the game UI representation, square has event-handler for moves
     * @param x canvas x coordinate
     * @param y canvas y coordinate
     * @param p point3D associated with square {x:0, y:0, z:0}
     * @private
     */
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
                this.updateAvailable()
            } else {
                console.log("selected")
                // request available moves
                this.setState({selectedPos: point})
                this.state.connection.sendAvailMovesRequest(point, this.state.player)
                this.updateAvailable()
            }
        }

        square.interactive = true
        square.on('touchstart', click);
        square.on("mousedown", click)
        this.state.pixi.stage.children[0].addChild(square)
    }

    render() {
        return (
            <Container className="Game" fluid>
                <Row>Game</Row>
                <Row ref={(el) => {
                    this.GView = el
                }}/>
            </Container>
        );
    }
}

export default Game;
