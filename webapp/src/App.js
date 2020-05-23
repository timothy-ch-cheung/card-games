import React from 'react';
import Navigation from "./components/Navigation";
import {BrowserRouter as Router} from "react-router-dom";

function App() {
    return (
        <Router>
            <Navigation/>
        </Router>
    );
}

export default App;
