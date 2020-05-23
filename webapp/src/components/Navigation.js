import React from "react";
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar'
import {Route, Switch} from "react-router-dom";
import '../App.css';
import Home from "../home/Home";
import Games from "../games/Games";
import {NavLink} from "react-bootstrap";
import {LinkContainer} from 'react-router-bootstrap'

class Navigation extends React.Component {

    render() {
        return (
            <div>
                <Navbar bg="dark" variant="dark" expand="lg">
                    <Navbar.Brand href="/">Noughts and Crosses</Navbar.Brand>
                    <Navbar.Toggle aria-controls="basic-navbar-nav"/>
                    <Navbar.Collapse id="basic-navbar-nav">
                        <Nav className="mr-auto">
                            <LinkContainer to="/home"><NavLink>Home</NavLink></LinkContainer>
                            <LinkContainer to="/games"><NavLink>Game</NavLink></LinkContainer>
                        </Nav>
                    </Navbar.Collapse>
                </Navbar>
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
            </div>
        );
    }
}

export default Navigation;