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
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
import GroupSelector from "./GroupSelector.jsx";
import Chat from "../Chat.jsx"
import Connection from "../../classes/Connection";
import {setConnectionAction, setGameIdAction} from "../../redux/actions";
import {connect} from 'react-redux'
import GameReady from "./GameReady.jsx";

const mapDispatchToProps = (dispatch) => {
    return {
        storeSetConnection: (connection) => dispatch(setConnectionAction(connection)),
        storeSetGameId: (gameId) => dispatch(setGameIdAction(gameId))
    }
}

class GameConfiguration extends Component {

    componentDidMount() {
        let gameId = this.props.match.params.gameId
        this.socket_url = "ws://localhost:9000/socket?id=" + gameId
        this.connection = new Connection(this.socket_url)
        this.props.storeSetConnection(this.connection)
        this.props.storeSetGameId(gameId)
    }


    render() {
        return (
            <Container className={"GameConfiguration"}>
                <Row>
                    <Col md="auto">
                        <GroupSelector nrGroups={2}/>
                    </Col>
                    <Col>
                        <Chat/>
                    </Col>
                </Row>
                <Row className={"mt-4"}>
                    <Col>
                        <GameReady/>
                    </Col>
                </Row>
            </Container>
        );
    }

}

export default connect(null, mapDispatchToProps)(GameConfiguration)
