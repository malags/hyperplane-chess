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
import {setNameAction} from "../redux/actions";

//TODO

const _setName = (dispatch, e) => {
    let newName = e.target.value
    if (newName.length < 15) dispatch(setNameAction(newName))
}

function mapStateToProps(state) {
    return {
        name: state.player.name,
        player: state.player,
        connection: state.connection
    }
}


const mapDispatchToProps = (dispatch) => {
    return {
        setName: (control) => _setName(dispatch, control)
    }
}

class Chat extends Component {

    setName = (e) => {
        let newName = e.target.value
        let player = {...this.props.player}
        player.name = newName
        this.props.connection.sendSetPlayer(player)
    }

    render() {
        return (
            <Container className={"Chat"}>
                <Col>
                    <h1>Chat</h1>
                    <Form inline>
                        <Form.Group as={Row}>
                            <Form.Label column>Chat Name</Form.Label>
                            <Col xs="auto">
                                <Form.Control type={"input"} placeholder={"chat name"} value={this.props.name}
                                              onChange={this.setName}/>
                            </Col>
                        </Form.Group>
                    </Form>

                    <ChatMessages/>
                </Col>
            </Container>
        );
    }
}

export default connect(mapStateToProps, null)(Chat)
