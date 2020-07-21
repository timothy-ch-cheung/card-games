import React from "react";
import ButtonGroup from "react-bootstrap/ButtonGroup";
import Button from "react-bootstrap/Button";
import './Home.css';
import {LinkContainer} from 'react-router-bootstrap'
import CreateGame from "../components/create-game/CreateGame";
import Image from "react-bootstrap/Image";

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
                <div style={{width:"100%", display:"flex", justifyContent: "center", marginTop:"10px", marginBottom:"10px"}}>
                    <Image className={"no-drag"} src="card-banner.svg" fluid style={{width:"70%"}}/>
                </div>
                <h1>PLAY:</h1>
                <div className="button-panel">
                    <ButtonGroup aria-label="Basic example">
                        <Button size="xl" variant="info" onClick={e => {
                            this.handleShow();
                        }}>Create Game</Button>
                        <CreateGame show={this.state.show} onClose={this.handleHide}/>
                        <LinkContainer to="/games/public">
                            <Button size="xl" variant="light">Join Game</Button>
                        </LinkContainer>
                    </ButtonGroup>
                </div>
            </div>
        )
    }
}

export default Home