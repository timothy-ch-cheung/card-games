import React from "react";
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar'
import '../../App.css';
import {NavLink} from "react-bootstrap";
import {LinkContainer} from 'react-router-bootstrap'
import Image from "react-bootstrap/Image";

class Navigation extends React.Component {

    render() {
        return (
            <Navbar bg="dark" variant="dark" expand="lg">
                <LinkContainer to="/">
                    <Navbar.Brand className={"no-drag"}>
                        <Image
                            src="logo.svg"
                            width="32"
                            height="32"
                            className="d-inline-block align-top no-drag"
                        />
                        {' '}Card Games</Navbar.Brand>
                </LinkContainer>
                <Navbar.Toggle aria-controls="basic-navbar-nav"/>
                <Navbar.Collapse>
                    <Nav className="mr-auto">
                        <LinkContainer to="/home"><NavLink className={"no-drag"}>Home</NavLink></LinkContainer>
                        <LinkContainer to="/games"><NavLink className={"no-drag"}>Games</NavLink></LinkContainer>
                    </Nav>
                </Navbar.Collapse>
            </Navbar>
        );
    }
}

export default Navigation;