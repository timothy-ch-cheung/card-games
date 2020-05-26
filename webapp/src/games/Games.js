import React from "react";
import LobbyCard from "../components/LobbyCard";
import styled from "styled-components";
import CreateGame from "../components/CreateGame";
import Button from "react-bootstrap/Button";

const Divider = styled.div`
border-bottom: 1px solid grey;
margin: 10px;
`;


class Games extends React.Component {

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
                        .left-title {
                            text-align: left;
                        }
                        
                        .btn-xl {
                            font-size: 1.5rem;
                            border: grey solid 1px;
                            width: 200px;
                            margin: 20px 10px 30px;
                        }
                    `}
                </style>
                <Button size="xl" variant="info" onClick={e => {
                    this.handleShow();
                }}>Create Game</Button>
                <CreateGame show={this.state.show} onClose={this.handleHide}/>
                <Divider/>
                <h1 className="left-title">Public games</h1>
                <LobbyCard/>
            </div>
        )
    }
}

export default Games;