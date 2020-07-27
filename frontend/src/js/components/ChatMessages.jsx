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
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Button from "react-bootstrap/Button";
import {connect} from 'react-redux'
import {gotMessageAction} from "../redux/actions";

const mapStateToProps = (state) => {
    return {
        name: state.name,
        messages: state.messages,
        break_size: state.break_size,
        connection: state.connection
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        gotMessage: (message) => dispatch(gotMessageAction(message))
    }
}

class ChatMessages extends Component {

    state = {
        draftMessage: "",
        idCounter: 0,
    }


    messageChange = (e) => {
        this.setState({
            draftMessage: e.target.value
        })
    }

    sendMessage = () => {
        let name = this.props.name
        let id = (this.state.idCounter + 1) % this.props.break_size
        let message = {
            id: name + "_id_" + id,
            content: this.state.draftMessage,
            sender: name,
        }
        this.setState({
            draftMessage: "",
            idCounter: id,
        })

        this.props.connection.sendMessage(message)
    }
    handleKeys = (event) => {
        let key = event.key
        if (key === "Enter") this.sendMessage()
    };


    render() {
        return (
            <Col>{
                this.props.messages.map(message =>
                    <Row key={message.id}>{message.sender + ": " + message.content}</Row>
                )
            }
                <Row>
                    <input type={"text"} value={this.state.draftMessage} onChange={this.messageChange}
                           onKeyPress={this.handleKeys}/><Button
                    onClick={this.sendMessage}>Send</Button>
                </Row>
            </Col>
        );
    }

}


export default connect(mapStateToProps, mapDispatchToProps)(ChatMessages)

