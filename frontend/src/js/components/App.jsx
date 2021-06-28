/*
 * Copyright © 2021 Stefano Malagò
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import React, {Component} from "react";
import {Provider, ReactReduxContext} from "react-redux"
import ReactDOM from "react-dom";

import Game from "./Game.jsx";
import NewGameForm from "./NewGameForm.jsx";
import {Route, Switch} from "react-router-dom";
import {ConnectedRouter} from 'connected-react-router'
import NavBar from "./NavBar.jsx";
import Home from "./Home.jsx";
import GameConfiguration from "./game-configuration/GameConfiguration.jsx";

import {history} from "../redux/configureStore.js";
import {store} from "../redux/Store.js";

ReactDOM.render(
    <Provider store={store}>
        <ConnectedRouter history={history}>
            <div className="App">
                <NavBar/>
                <Switch>
                    <Route exact path="/" component={Home}/>
                    <Route path={"/game"} component={Game}/>
                    <Route path={"/new-game"} component={NewGameForm}/>
                    <Route path={"/connect/:gameId"} component={GameConfiguration}/>
                </Switch>
            </div>
        </ConnectedRouter>
    </Provider>,
    document.getElementById('root')
)

