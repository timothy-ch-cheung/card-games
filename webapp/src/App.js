import React from 'react';
import Navigation from "./components/Navigation";
import {BrowserRouter as Router, Route, Switch} from "react-router-dom";
import Home from "./home/Home";
import Games from "./games/Games";

function App() {
    return (
        <Router>
            <Navigation/>
            <div>
                <Switch>
                    <Route exact path='/' component={Home}/>
                    <Route exact path='/home' component={Home}/>
                    <Route exact path='/games' component={Games}/>
                    <Route render={function () {
                        return <p>Not found</p>
                    }}/>
                </Switch>
            </div>
        </Router>
    );
}

export default App;
