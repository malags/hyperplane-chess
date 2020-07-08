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

class NewGameForm extends Component {

    render() {
        return (
            <Container className="NewGameForm" fluid>
                <Form>
                    <Form.Group controlId="newGameFrom.nrBoards">
                        <Form.Label>Number of Boards</Form.Label>
                        <Form.Control as="select">
                            {_.range(1, 8 + 1).map(value => <option key={value} value={value}>{value}</option>)}
                        </Form.Control>
                        <Form.Text className="text-muted">
                            It's possible to play with up to 8 boards
                        </Form.Text>
                    </Form.Group>
                    <Form.Group controlId="newGameFrom.nrPlayers">
                        <Form.Label>Number of Players</Form.Label>
                        <Form.Control as="select">
                            {_.range(2, 16 + 1).map(value => <option key={value} value={value}>{value}</option>)}
                        </Form.Control>
                        <Form.Text className="text-muted">
                            It's possible to play with up to 16 players
                        </Form.Text>
                    </Form.Group>

                    <Button variant="primary" type="submit">
                        Submit
                    </Button>
                </Form>
            </Container>
        );
    }

}

export default NewGameForm;
