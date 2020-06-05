import React from 'react';
import Navigation from "./components/navigation/Navigation";
import {BrowserRouter as Router, Route, Switch} from "react-router-dom";
import Home from "./home/Home";
import Games from "./games/Games";
import Lobby from "./lobby/Lobby";

function App() {
    return (
        <Router>
            <Navigation/>
            <div>
                <Switch>
                    <Route exact path='/' component={Home}/>
                    <Route exact path='/home' component={Home}/>
                    <Route exact path='/games' component={Games}/>
                    <Route exact path='/current-game' component={Lobby}/>
                    <Route render={function () {
                        return <p>Not found</p>
                    }}/>
                </Switch>
            </div>
        </Router>
    );
}

export default App;
