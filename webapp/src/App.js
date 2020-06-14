import React, {useState} from 'react';
import Navigation from "./components/navigation/Navigation";
import {BrowserRouter as Router, Route, Switch} from "react-router-dom";
import Home from "./home/Home";
import Games from "./games/Games";
import Lobby from "./lobby/Lobby";
import GameAlert from "./components/game-alert/GameAlert";

function App() {
    const [errorText, setErrorText] = useState("Unknown")
    const [errorShow, setShowError] = useState(false);

    const onShowError = (text) => {
        setErrorText(text);
        setShowError(true);
        setTimeout(() => {setShowError(false)}, 5000);
    }

    return (
        <Router>
            <Navigation/>
            <GameAlert show={errorShow} errorText={errorText} onClose={() => setShowError(false)}/>
            <div>
                <Switch>
                    <Route exact path='/' component={Home}/>
                    <Route exact path='/home' component={Home}/>
                    <Route exact path='/games' component={Games}/>
                    <Route exact path='/current-game' render={props => (<Lobby onShowError={onShowError}/>)}/>
                    <Route render={function () {
                        return <p>Not found</p>
                    }}/>
                </Switch>
            </div>
        </Router>
    );
}

export default App;
