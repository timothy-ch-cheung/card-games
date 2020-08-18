import React, {useEffect, useState} from 'react';
import Card from "react-bootstrap/Card";
import NumberPicker from "../number-picker/NumberPicker";
import GameModes from "../../GameModes";
import Form from "react-bootstrap/Form";
import Button from "react-bootstrap/Button";

function LobbySettings(props) {
    const MAX_ROUNDS_MULTIPLIER = 4;
    const [validated, setValidated] = useState(false);

    const isSingleRoundIncrement = () => {
        return GameModes[props.gameMode] ? GameModes[props.gameMode].roundIncrement : undefined;
    }

    const getStep = (game) => {
        if (!game) {
            return;
        }

        let stepType = game.roundIncrement
        switch (stepType) {
            case "ONE":
                return 1;
            case "PLAYER_COUNT":
                return props.numPlayers;
            default:
                return undefined;
        }
    };

    const submitHandler = e => {
        e.preventDefault();
        const form = e.currentTarget;
        const valid = form.checkValidity();

        if (valid === false || !e.target.numRounds.value) {
            e.stopPropagation();
            setValidated(true);
            return;
        }

        setValidated(false);
        props.onSubmit();
    };

    let step = getStep(GameModes[props.gameMode])
    const [rounds, setRounds] = useState(1);
    const [totalRounds, setTotalRounds] = useState(step);

    const calculateTotalRounds = (step, rounds) => {
        return step * rounds;
    }

    const decrease = () => {
        if (GameModes[props.gameMode] && totalRounds > step) {
            setRounds(rounds - 1);
        }
    }

    const increase = () => {
        if (GameModes[props.gameMode] && totalRounds < (step * MAX_ROUNDS_MULTIPLIER)) {
            setRounds(rounds + 1);
        }
    }

    useEffect(() => {
        setTotalRounds(calculateTotalRounds(step, rounds))
    }, [step, rounds])

    const RoundPicker = () => {
        return (
            <div style={{display: "flex"}}>
                <div style={{marginRight: "5px"}}>
                    <Form.Label>{isSingleRoundIncrement() ? "Rounds" : "Stages"}</Form.Label>
                    <NumberPicker value={rounds} onIncrease={increase} onDecrease={decrease}
                                  name={"numRounds"} data-test={'round-number-picker'}/>
                </div>
                <div style={isSingleRoundIncrement() ? {display: "none"} : {display: "visible"}}>
                    <Form.Label>Rounds</Form.Label>
                    <Form.Control required type="text" name="totalRounds" disabled value={totalRounds}
                                  style={{backgroundColor: "#fff", textAlign: "center", width: "80px"}}
                                  data-test="round-number-text"/>
                </div>
            </div>
        );
    }

    return (
        <Card style={{width: "35%", margin: "10px"}}>
            <Card.Header style={{paddingLeft: "10px"}}>Game: {props.gameMode}</Card.Header>
            <Card.Body style={{padding: "5px 10px", height: "400px", overflowY: "auto"}}>
                <Form noValidate validated={validated} onSubmit={submitHandler} data-test={'lobby-settings-form'}>
                    <RoundPicker/>
                </Form>
            </Card.Body>
            <Card.Body style={{borderTop: "1px solid #dfdfdf", padding: "10px"}}>
                <Button variant="info" type="submit" data-test={"start-game-btn"}>Start</Button>
            </Card.Body>
        </Card>
    );
}

export default LobbySettings;
