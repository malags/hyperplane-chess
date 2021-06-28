/*
 * Copyright © 2021 Stefano Malagò
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import React, {Component} from "react";
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Button from "react-bootstrap/Button";
import GameReady from "./GameReady.jsx";
import {connect} from 'react-redux'

const mapStateToProps = (state) => {
    return {
        nrGroups: state.gameConfig.nrGroups,
        connection: state.connection,
        players: state.gameConfig.players,
        player: state.gameConfig.player,
        name: state.gameConfig.player.name,
        playersReady: state.gameConfig.playersReady
    }
}

class GroupSelector extends Component {

    componentDidMount() {
        setTimeout(() => {
            this.props.connection.socket.addEventListener(
                "open", () => {
                    this.props.connection.sendNewPlayerRequest()
                    this.props.connection.sendGetAllPlayers()
                    this.props.connection.sendGetAllReadyStatus()
                })
        }, 0)
    }


    /**
     * User selected a group, notify other players
     * @param groupId Id of the group selected
     */
    select = (groupId) => {
        let player = {...this.props.player}
        let connection = this.props.connection
        GameReady.setNotReady(connection, player)
        player.groupId = groupId
        connection.sendSetPlayer(player)
    }

    _player = (groupId) => {
        let players = this.props.players
        let ready = this.props.playersReady
        if (players === undefined || players.length === 0) return
        else return (
            <Col key={groupId}>{
                players
                    .filter(p => p.groupId === groupId)
                    .map(player => {
                        let index = player.playerId // ready state is sorted by playerId
                        let color = ready[index] ? "green" : "red"
                        let readyText = ready[index] ? "✓" : "✗"
                        return <div><span key={player.name}>{player.name}</span><span key={player.name + "_ready"}
                                                                                      style={{"color": color}}
                        >{"\t\t" + readyText}</span></div>
                    })}
            </Col>)
    }

    /**
     * Iterate over all groups and returns an array with f applied, i.e. [f(0), f(1), ..., f(this.props.nrGroups-1) ]
     * @param f the function to apply at each index
     * @returns [f(0), f(1), ..., f(this.props.nrGroups-1) ]
     * @private
     */
    _create = (f) => {
        let elements = []
        for (let i = 0; i < this.props.nrGroups; ++i)
            elements.push(f(i))
        return elements

    }

    _players = () => {
        return this._create(this._player)
    }

    _groups = () => {
        return this._create((i) => {
            return <Col key={"group_h_" + i}>
                <h5>Group {i + 1}</h5>
            </Col>
        })
    }

    _buttons = () => {
        return this._create((i) => {
            return <Col key={"button_" + i}>
                <Button onClick={() => this.select(i)}>Join</Button>
            </Col>
        })
    }


    render() {
        return (
            <Container className={"mx-auto"}>
                <Row><h1>Group Selector</h1></Row>
                <Col sm>
                    <Row>
                        {this._groups()}
                    </Row>
                    <Row style={{
                        "maxHeight": window.screen.height * 0.5,
                        "minHeight": window.screen.height * 0.5
                    }}>
                        {this._players()}
                    </Row>
                    <Row>
                        {this._buttons()}
                    </Row>
                </Col>
            </Container>
        );
    }
}

export default connect(mapStateToProps)(GroupSelector)
