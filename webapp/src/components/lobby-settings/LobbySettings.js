import React from 'react';
import Card from "react-bootstrap/Card";

function LobbySettings(props) {

    return (
        <Card style={{width: "35%", margin: "10px"}}>
            <Card.Header>Game: {props.gameMode}</Card.Header>
            <Card.Body style={{padding: "5px 5px", height: "400px", overflowY: "auto"}}>
            </Card.Body>
        </Card>
    );
}

export default LobbySettings;
