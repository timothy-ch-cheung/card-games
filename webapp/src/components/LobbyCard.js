import React from 'react';
import Card from "react-bootstrap/Card";
import Button from "react-bootstrap/Button";

function LobbyCard(props) {
    return (
        <div>
            <style type="text/css">
                {`
                    .lobby-card {
                        margin: 5px;
                    }
                `}
            </style>
            <Card className="lobby-card" style={{width: '18rem'}}>
                <Card.Body>
                    <Card.Title>{props.lobbyName}</Card.Title>
                    <Card.Text>
                        Host: {props.host}
                    </Card.Text>
                    <Button variant="info">Join Game</Button>
                </Card.Body>
            </Card>
        </div>
    );
}

export default LobbyCard;