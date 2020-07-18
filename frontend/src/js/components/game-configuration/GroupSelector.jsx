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
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Button from "react-bootstrap/Button";

class GroupSelector extends Component {
    state = {
        playerName: "",
        playersPerGroup: []
    }

    componentDidMount() {
        let nrGroups = this.props.nrGroups
        let sets = new Array(nrGroups).fill().map(() => new Set())

        let configConnection = this.props.configConnection
        configConnection.addPlayerToGroup = this.addPlayerToGroup
        configConnection.removePlayerFromGroup = this.removePlayerFromGroup

        this.setState({
            playersPerGroup: sets
        })
        this.select(0)

    }


    /**
     * Add a Player from the group, called from connection
     * @param playerName player name to add to the list
     * @param groupId index of the group
     */
    addPlayerToGroup(playerName, groupId) {
        let playersPerGroup = [...this.state.playersPerGroup]
        playersPerGroup[groupId].add(playerName)

        this.setState({
            playersPerGroup: playersPerGroup
        })
    }

    /**
     * Remove a Player from the group, called from connection
     * @param playerName player name to remove from the list
     * @param groupId index of the group
     */
    removePlayerFromGroup(playerName, groupId) {
        let playersPerGroup = [...this.state.playersPerGroup]
        playersPerGroup[groupId].remove(playerName)

        this.setState({
            playersPerGroup: playersPerGroup
        })
    }

    /**
     * User selected a group, notify other players
     * @param e button click event
     */
    select = (groupId) => {
        //TODO send data
    }

    _player = (groupId) => {
        let setsArray = this.state.playersPerGroup
        if (setsArray === undefined || setsArray.length === 0) return
        else return (
            <Col key={groupId}>{
                [...setsArray[groupId]].map(player => <p key={player}>{player}</p>)}
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
                <Button onClick={this.select(i)}>Join</Button>
            </Col>
        })
    }


    render() {
        return (
            <Container className={"mx-auto"}>
                <Row><h1>GroupSelector</h1></Row>
                <Container>
                    <Row>
                        {this._groups()}
                    </Row>
                    <Row>
                        {this._players()}
                    </Row>
                    <Row>
                        {this._buttons()}
                    </Row>
                </Container>
            </Container>
        );
    }
}

export default GroupSelector
