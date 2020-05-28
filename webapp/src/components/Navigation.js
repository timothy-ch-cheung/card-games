import React from "react";
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar'
import '../App.css';
import {NavLink} from "react-bootstrap";
import {LinkContainer} from 'react-router-bootstrap'
import {useSelector, useDispatch} from "react-redux";

class Navigation extends React.Component {

    render() {
        return (
            <Navbar bg="dark" variant="dark" expand="lg">
                <LinkContainer to="/">
                    <Navbar.Brand>Noughts and Crosses</Navbar.Brand>
                </LinkContainer>
                <Navbar.Toggle aria-controls="basic-navbar-nav"/>
                <Navbar.Collapse>
                    <Nav className="mr-auto">
                        <LinkContainer to="/home"><NavLink>Home</NavLink></LinkContainer>
                        <LinkContainer to="/games"><NavLink>Games</NavLink></LinkContainer>
                    </Nav>
                </Navbar.Collapse>
            </Navbar>
        );
    }
}

export default Navigation;