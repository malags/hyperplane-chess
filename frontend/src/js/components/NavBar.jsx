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
import Navbar from "react-bootstrap/Navbar";
import Nav from "react-bootstrap/Nav";
import {Link, NavLink} from "react-router-dom";
import Form from "react-bootstrap/Form";
import Button from "react-bootstrap/Button";
import {connect} from 'react-redux'
import {setGameIdAction} from "../redux/actions";

const mapStateToProps = (state) => {
    return {
        gameId: state.gameConfig.gameId
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        setGameId: (gameId) => dispatch(setGameIdAction(gameId))
    }
}

class NavBar extends Component {
    selectGameId = (e) => {
        let gameId = -1
        if (e.target.value !== "") gameId = parseInt(e.target.value)
        this.props.setGameId(gameId)
    }

    render() {
        return (
            <Navbar bg="dark" variant="dark" expand="lg">
                <Navbar.Brand to="/" as={Link}>Hyperplane Chess</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav"/>
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="mr-auto">
                        <Nav.Link to="/new-game" as={NavLink}>New Game</Nav.Link>
                    </Nav>
                    <Form inline>
                        <Form.Control type="number" placeholder="Game ID" className="mr-sm-2"
                                      onChange={this.selectGameId}/>
                        <Button variant="outline-success" to={"/connect/" + this.props.gameId}
                                as={Link}>Connect</Button>
                    </Form>
                </Navbar.Collapse>
            </Navbar>
        )
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(NavBar)
