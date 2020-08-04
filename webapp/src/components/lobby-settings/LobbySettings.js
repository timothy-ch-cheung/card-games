import React, {useState} from 'react';
import Card from "react-bootstrap/Card";
import NumberPicker from "../number-picker/NumberPicker";
import GameModes from "../../GameModes";

function LobbySettings(props) {
    const [numPlayers, setNumPlayers] = useState(2);

    const getStep = (stepType) => {
        switch (stepType) {
            case "ONE":
                return 1;
            case "PLAYER_COUNT":
                return numPlayers;
        }
    }

    let step = getStep(GameModes[props.gameMode].rounds.increment)

    return (
        <Card style={{width: "35%", margin: "10px"}}>
            <Card.Header>Game: {props.gameMode}</Card.Header>
            <Card.Body style={{padding: "5px 5px", height: "400px", overflowY: "auto"}}>
                <NumberPicker value={numPlayers} minValue={GameModes[props.gameMode].minPlayers}
                              maxValue={GameModes[props.gameMode].maxPlayers} setValue={setNumPlayers}
                              step={step}/>
            </Card.Body>
        </Card>
    );
}

export default LobbySettings;
