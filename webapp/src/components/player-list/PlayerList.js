import React from 'react';
import Card from "react-bootstrap/Card";
import Button from "react-bootstrap/Button";
import Table from "react-bootstrap/Table";
import {isBlank} from "../../common/Util";

function Player(props) {
    let text = props.isHost ? props.name + " 👑" : props.name;

    return (
        <p data-test={props.dataTest} style={{marginBottom: "0"}}>{text}</p>
    );
}


function PlayerList(props) {

    const renderPlayer = (card, index) => {
        if (!isBlank(card.username)) {
            return (
                <tr key={index}>
                    <td style={{padding: "5px"}}>
                        <Player name={card.username} isHost={card.isHost} key={index}
                                dataTest={"player" + index + "-name"} data-test={"player" + index + "-row"}/>
                    </td>
                </tr>);
        }
        return null;
    }

    return (
        <Card style={{width: "35%", margin: "10px"}}>
            <Card.Header style={{display: "flex", justifyContent: "space-between", paddingLeft: "10px"}}>
                <p style={{marginBottom: "0"}}>Lobby</p>
                <p style={{marginBottom: "0"}} data-test="player-count">{props.players.length}/{props.maxPlayers}</p></Card.Header>
            <Card.Body style={{padding: "5px 5px", height: "400px", overflowY: "auto"}}>
                <Table borderless variant="light">
                    <tbody>
                    {props.players.map(renderPlayer)}
                    </tbody>
                </Table>
            </Card.Body>
            <Card.Body style={{borderTop: "1px solid #dfdfdf", padding: "10px"}}>
                <Button variant="info" className="leave-game" onClick={props.onLeave}
                        data-test="leave-game-btn">Leave</Button>
            </Card.Body>

        </Card>
    );
};

export default PlayerList;