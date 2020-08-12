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
import ChatMessages from "./ChatMessages.jsx"
import Form from "react-bootstrap/Form";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
import {connect} from 'react-redux'
import GameReady from "./game-configuration/GameReady.jsx";

//TODO

function mapStateToProps(state) {
    return {
        name: state.player.name,
        player: state.player,
        connection: state.connection
    }
}

class Chat extends Component {

    setName = (e) => {
        let newName = e.target.value
        let player = {...this.props.player}
        let connection = this.props.connection
        player.name = newName
        connection.sendSetPlayer(player)
        GameReady.setNotReady(connection, player)
    }

    render() {
        return (
            <Container className={"Chat"}>
                <Row>
                    <Col>
                        <h1>Chat</h1>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <Form inline>
                            <Form.Group as={Row}>
                                <Form.Label column>Chat Name</Form.Label>
                                <Form.Control type={"input"} placeholder={"chat name"} value={this.props.name}
                                              onChange={this.setName}/>
                            </Form.Group>
                        </Form>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <ChatMessages/>
                    </Col>
                </Row>
            </Container>
        );
    }
}

export default connect(mapStateToProps)(Chat)
