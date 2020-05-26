import React from "react";
import ButtonGroup from "react-bootstrap/ButtonGroup";
import Button from "react-bootstrap/Button";
import './Home.css';
import {LinkContainer} from 'react-router-bootstrap'
import Board from "../components/Board";
import CreateGame from "../components/CreateGame";

class Home extends React.Component {

    state = {
        show: false
    };

    handleShow = () => {
        this.setState({show: true});
    }

    handleHide = () => {
        this.setState({show: false});
    }

    render() {
        return (
            <div>
                <style type="text/css">
                    {`
                        .btn-xl {
                            font-size: 1.5rem;
                            border: grey solid 1px;
                            width: 200px;
                        }
                        
                        @media screen and (max-width: 768px) {
                            .btn-xl {
                                font-size: 1.2rem;
                                width: 150px;
                            }
                        }
                    `}
                </style>
                <h1>Play against a Bot:</h1>
                <Board/>
                <h1>Play with others:</h1>
                <div className="button-panel">
                    <ButtonGroup aria-label="Basic example">
                        <Button size="xl" variant="info" onClick={e => {
                            this.handleShow();
                        }}>Create Game</Button>
                        <CreateGame show={this.state.show} onClose={this.handleHide}/>
                        <LinkContainer to="/games">
                            <Button size="xl" variant="light">Join Game</Button>
                        </LinkContainer>
                    </ButtonGroup>
                </div>
            </div>
        )
    }
}

export default Home