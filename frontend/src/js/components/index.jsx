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
import ReactDOM from "react-dom";

import Game from "./Game.jsx";
import NewGameForm from "./NewGameForm.jsx";
import Button from "react-bootstrap/Button";
import Container from "react-bootstrap/Container";

class App extends Component {
    constructor(props) {
        super(props);
    }

    newGame(e) {
        console.log(e.target)
    }

    render() {
        return (
            <div className="app-content">
                <Container>
                    <h1>Hello World!</h1>
                    <Button onClick={this.newGame.bind(this)}>new Game</Button>
                </Container>
                <Game socket_url={window.location.href.replace("http", "ws") + "socket?id=" + 0}/>
                {/*TODO: change to dynamic*/}
                <NewGameForm/>
                <link
                    rel="stylesheet"
                    href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css"
                    integrity="sha384-9aIt2nRpC12Uk9gS9baDl411NQApFmC26EwAOH8WgZl5MYYxFfc+NcPb1dKGj7Sk"
                    crossOrigin="anonymous"
                />
            </div>
        )
    }
}

export default App;

const wrapper = document.getElementById("app");
wrapper ? ReactDOM.render(<App/>, wrapper) : false;
