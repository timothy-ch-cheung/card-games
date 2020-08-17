import React from 'react';
import Card from "react-bootstrap/Card";
import Button from "react-bootstrap/Button";
import {useSelector} from "react-redux";

function LobbyCard(props) {
    const userId = useSelector(state => state.user);
    const userKey = useSelector(state => state.key);

    const onJoinGame = () => {
        if (userId != null) {
            props.onSubmit(props.gameId, userId, userKey);
        } else {
            props.showModal(props.gameId);
        }
    }

    return (
        <>
            <style type="text/css">
                {`
                    .lobby-card {
                        margin: 5px;
                        width: 18rem;
                        flex-basis: 24%;
                    }
                    
                    @media(max-width: 1080px) {
                        .lobby-card {
                            flex-basis: 32%;
                        }
                    }
                    
                    @media(max-width: 800px) {
                        .lobby-card {
                            flex-basis: 48%;
                        }
                    }
                    
                    @media(max-width: 550px) {
                        .lobby-card {
                            flex-basis: 99%;
                        }
                    }
                    
                    .card-title {
                        border-bottom: solid 1px #d6d6d6;
                    }
                    
                `}
            </style>
            <Card className="lobby-card">
                <Card.Body>
                    <Card.Title className="card-title">{props.lobbyName}</Card.Title>
                    <Card.Text style={{marginBottom: "5px"}}>
                        Host: {props.host}
                    </Card.Text>
                    <Card.Text style={{fontSize:"0.8rem", color:"#707070"}}>
                        Players: {props.numPlayers}/{props.maxPlayers}
                    </Card.Text>
                    <Button variant="info" onClick={onJoinGame} data-test={`join-game-btn-${props.gameId}`}>Join Game</Button>
                </Card.Body>
            </Card>
        </>
    );
}

export default LobbyCard;