import React from "react";
import ButtonGroup from "react-bootstrap/ButtonGroup";
import Button from "react-bootstrap/Button";
import './Home.css';
import {LinkContainer} from 'react-router-bootstrap'

class Home extends React.Component {
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
                <div className="button-panel">
                    <ButtonGroup aria-label="Basic example">
                        <Button size="xl" variant="info">Create Game</Button>
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