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
import Form from "react-bootstrap/Form";
import Button from "react-bootstrap/Button";
import _ from 'lodash';
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";

class NewGameForm extends Component {
    url = 'http://localhost:9000/newGame' //window.location.origin + "/newGame"

    state = {
        nrBoards: 1,
        boardSize: 5,
        nrPlayers: 2,
        nrGroups: 2,
        movementFile: "",
        piecesPosition: new Array(10).fill("")
    }

    onChangeInt = (e) => {
        this.setState({
            [e.target.id]: parseInt(e.target.value)
        })

        // re-initialize piecesPosition
        if (e.target.id.valueOf() === "boardSize") {
            this.setState({
                piecesPosition: new Array(e.target.value * Math.floor(e.target.value / 2)).fill("")
            })
        }
    };

    onChangeString = (e) => {
        this.setState({
            [e.target.id]: e.target.value
        })
    };

    onChangePosition = (e) => {
        let tempPos = this.state.piecesPosition
        let idx = e.target.getAttribute("idx")
        tempPos[idx] = e.target.value
        this.setState({piecesPosition: tempPos})
    };

    mySubmitHandler = (event) => {
        event.preventDefault();

        let request = {
            method: 'POST',
            headers: {
                'Accept': 'application/json'
            },

            body: JSON.stringify({
                "nrBoards": this.state.nrBoards,
                "boardSize": this.state.boardSize,
                "nrPlayers": this.state.nrPlayers,
                "nrGroups": this.state.nrGroups,
                "piecesPosition": this.state.piecesPosition,
                "movementFile": JSON.parse(this.state.movementFile)
            })
        };
        fetch(this.url, request)
            .then(response => {
                if (response.status >= 200 && response.status < 300) {
                    console.log(response)
                    window.location.reload();
                } else {
                    console.log(response)
                    response.text().then(error => alert(error))

                }
            }).catch(err => console.log(err));
    }

    render() {
        return (
            <Container className="NewGameForm" fluid>
                <Form id={"formNewGame"} action={this.url} method={"POST"}
                      onSubmit={this.mySubmitHandler}>
                    <Form.Group controlId="nrBoards">
                        <Form.Label>Number of Boards</Form.Label>
                        <Form.Control as="select" value={this.state.nrBoards} onChange={this.onChangeInt}>
                            {_.range(1, 8 + 1).map(value => <option key={value} value={value}>{value}</option>)}
                        </Form.Control>
                        <Form.Text className="text-muted">
                            It's possible to play with up to 8 boards.
                        </Form.Text>
                    </Form.Group>
                    <Form.Group controlId="boardSize">
                        <Form.Label>Size of each Board</Form.Label>
                        <Form.Control as="select" value={this.state.boardSize} onChange={this.onChangeInt}>
                            {_.range(5, 12 + 1).map(value => <option key={value} value={value}>{value}</option>)}
                        </Form.Control>
                        <Form.Text className="text-muted">
                            The Size of each Board in the Game
                        </Form.Text>
                    </Form.Group>
                    <Form.Group controlId="nrPlayers">
                        <Form.Label>Number of Players</Form.Label>
                        <Form.Control as="select" value={this.state.nrPlayers} onChange={this.onChangeInt}>
                            {_.range(2, 2 * this.state.nrBoards + 1).map(value => <option key={value}
                                                                                          value={value}>{value}</option>)}
                        </Form.Control>
                        <Form.Text className="text-muted">
                            It's possible to play with up to 16 Players.
                        </Form.Text>
                    </Form.Group>

                    <Form.Group controlId="nrGroups">
                        <Form.Label>Number of Groups</Form.Label>
                        <Form.Control as="select" value={this.state.nrGroups} onChange={this.onChangeInt}>
                            {_.range(2, this.state.nrPlayers + 1).map(value => <option key={value}
                                                                                       value={value}>{value}</option>)}
                        </Form.Control>
                        <Form.Text className="text-muted">
                            Players with Different Groups are considered Enemies.
                        </Form.Text>
                    </Form.Group>

                    <Form.Group controlId="movementFile">
                        <Form.Label>Pieces Definition</Form.Label>
                        <Form.Control required as="textarea" value={this.state.movementFile}
                                      onChange={this.onChangeString} rows={5}/>
                        <Form.Text className="text-muted">
                            Definition of the Pieces as specified on the <a
                            href={"https://github.com/malags/hyperplane-chess"} target={"_blank"}>GitHub page</a>.
                        </Form.Text>
                    </Form.Group>

                    <Form.Group controlId={"piecesPosition"}>
                        <Form.Label>Position of the Pieces</Form.Label>
                        <Container>
                            {this.boardConfig()}
                        </Container>
                    </Form.Group>

                    <Button variant="primary" type="submit">
                        Submit
                    </Button>
                </Form>
            </Container>
        );
    }

    boardConfig = () => {
        return _.range(0, this.state.boardSize).map(row => {
            return <Row key={"row_" + row}>
                {_.range(0, this.state.boardSize).map(column => {
                    return <Col key={"col_" + column}>
                        {this.boardTile(row, column)}
                    </Col>
                })
                }
            </Row>
            }
        )
    }

    boardTile = (row, column) => {
        let idx = (this.state.boardSize - row - 1) * this.state.boardSize + this.state.boardSize - column - 1
        if (row < this.state.boardSize / 2)
            return <Form.Control as="textarea"
                                 key={"key_" + column + "," + row}
                                 disabled
                                 value={""}/>
        else
            return <Form.Control as="textarea"
                                 onChange={this.onChangePosition}
                                 key={"key_" + column + "," + row}
                                 idx={idx}
                                 value={this.state.piecesPosition[idx]}/>
    }
}

export default NewGameForm;
