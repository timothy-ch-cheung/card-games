import React from "react";
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar'
import {Route, Switch} from "react-router-dom";
import '../App.css';
import NavLink from "./NavLink";

const Landing = () => (
    <div>
        <h1>Landing</h1>
    </div>
);

const Home = () => (
    <div>
        <h1>Home</h1>
    </div>
);

const Games = () => (
    <div>
        <h1>Games</h1>
    </div>
);

class Navigation extends React.Component {

    render() {
        return (
            <div>
                <Navbar bg="dark" variant="dark" expand="lg">
                    <Navbar.Brand href="/">Noughts and Crosses</Navbar.Brand>
                    <Navbar.Toggle aria-controls="basic-navbar-nav"/>
                    <Navbar.Collapse id="basic-navbar-nav">
                        <Nav className="mr-auto">
                            <NavLink to="/home" text="Home"/>
                            <NavLink to="/games" text="Games"/>
                        </Nav>
                    </Navbar.Collapse>
                </Navbar>
                <div>
                    <Switch>
                        <Route exact path='/' component={Landing}/>
                        <Route exact path='/home' component={Home}/>
                        <Route exact path='/games' component={Games}/>
                        <Route render={function () {
                            return <p>Not found</p>
                        }}/>
                    </Switch>
                </div>
            </div>
        );
    }
}

export default Navigation;