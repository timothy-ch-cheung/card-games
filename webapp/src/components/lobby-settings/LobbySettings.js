import React, {useState} from 'react';
import Card from "react-bootstrap/Card";
import NumberPicker from "../number-picker/NumberPicker";
import GameModes from "../../GameModes";
import Form from "react-bootstrap/Form";

function LobbySettings(props) {
    const MAX_ROUNDS_MULTIPLIER = 4;
    const [validated, setValidated] = useState(false);

    const getStep = (stepType) => {
        switch (stepType) {
            case "ONE":
                return 1;
            case "PLAYER_COUNT":
                return props.numPlayers;
        }
    };

    const submitHandler = e => {
        e.preventDefault();
        const form = e.currentTarget;
        const valid = form.checkValidity();

        if (valid === false) {
            e.stopPropagation();
            setValidated(true);
            return;
        }

        let numRounds = e.target.numRounds.value;
        console.log("Rounds: " + numRounds);

        setValidated(false);
        props.onClose();
    };

    let step = getStep(GameModes[props.gameMode].rounds.increment)
    const [rounds, setRounds] = useState(step);

    const decrease = () => {
        if (GameModes[props.gameMode] && rounds > step) {
            setRounds(rounds - step);
        }
    }

    const increase = () => {
        if (GameModes[props.gameMode] && rounds < (step * MAX_ROUNDS_MULTIPLIER)) {
            setRounds(rounds + step);
        }
    }

    return (
        <Card style={{width: "35%", margin: "10px"}}>
            <Card.Header>Game: {props.gameMode}</Card.Header>
            <Card.Body style={{padding: "5px 5px", height: "400px", overflowY: "auto"}}>
                <Form noValidate validated={validated} onSubmit={submitHandler}>
                    <Form.Label>Rounds</Form.Label>
                    <NumberPicker value={rounds} onIncrease={increase} onDecrease={decrease}
                                  name={"numRounds"}/>
                </Form>
            </Card.Body>
        </Card>
    );
}

export default LobbySettings;
