import React from 'react';
import Card from "react-bootstrap/Card";
import Button from "react-bootstrap/Button";
import Table from "react-bootstrap/Table";
import {isBlank} from "../../Util";

function Player(props) {
    let text = props.isHost ? props.name + " 👑" : props.name;

    return (
        <p style={{marginBottom: "0"}}>{text}</p>
    );

    return null;
}


function PlayerList(props) {

    const renderPlayer = (card, index) => {
        if (!isBlank(card.name)) {
            return (
                <tr>
                    <td style={{padding: "5px"}}><Player name={card.name} isHost={card.isHost} key={index}/></td>
                </tr>);
        }
        return null;
    }

    return (
        <Card style={{width: "35%", margin: "10px"}}>
            <Card.Header>Lobby</Card.Header>
            <Card.Body style={{padding: "5px 5px", height: "400px", overflowY: "auto"}}>
                <Table borderless variant="light">
                    <tbody>
                    {props.players.map(renderPlayer)}
                    </tbody>
                </Table>
            </Card.Body>
            <Card.Body style={{borderTop: "1px solid #dfdfdf", padding: "10px"}}>
                <Button variant="info" className="leave-game" onClick={props.onLeave}>Leave</Button>
            </Card.Body>

        </Card>
    );
};

export default PlayerList;