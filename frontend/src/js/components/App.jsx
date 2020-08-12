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
import {Provider} from "react-redux"
import ReactDOM from "react-dom";

import Game from "./Game.jsx";
import NewGameForm from "./NewGameForm.jsx";
import {BrowserRouter as Router, Route} from "react-router-dom";
import NavBar from "./NavBar.jsx";
import Home from "./Home.jsx";
import GameConfiguration from "./game-configuration/GameConfiguration.jsx";
import {render} from 'react-dom';

import store from "../redux/Store";


const App = () => (
    <Provider store={store}>
        <Router>
            <div className="App">
                <NavBar/>
                <Route exact path="/" component={Home}/>
                <Route path={"/game"} component={Game}/>
                <Route path={"/new-game"} component={NewGameForm}/>
                <Route path={"/connect/:gameId"} component={GameConfiguration}/>
            </div>
        </Router>
    </Provider>
)


render(<App/>, document.getElementById('root'));
