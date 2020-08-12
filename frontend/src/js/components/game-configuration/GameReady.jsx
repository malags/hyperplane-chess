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

import {connect} from "react-redux";
import React, {Component} from "react";
import Button from "react-bootstrap/Button";
import Col from "react-bootstrap/Col";
import Container from "react-bootstrap/Container";

const mapStateToProps = (state) => {
    return {
        player: state.player,
        playerId: state.player.playerId,
        connection: state.connection,
        playersReady: state.playersReady,
        players: state.players
    }
}

/**
 * Component containing the Button to toggle the ready state of the player, also contains setNotReady() to force ready=false
 */
class GameReady extends Component {

    /**
     * Set Player as not Ready
     * @param connection
     * @param player
     */
    static setNotReady = (connection, player) => {
        connection.sendSetReady(player, false)
    }

    /**
     * Toggle player's ready state, notify server
     */
    toggleReady = () => {
        let ready = !this.props.playersReady[this.props.playerId]
        this.props.connection.sendSetReady(this.props.player, ready)
    }

    render() {
        return (
            <Container>
                <Col>
                    <Button onClick={this.toggleReady}>Ready</Button>
                </Col>

            </Container>
        )
    }
}


export default connect(mapStateToProps)(GameReady)
