import * as React from "react";
import {useState} from "react";
import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";
import Form from "react-bootstrap/Form";
import {useDispatch, useSelector} from "react-redux";
import API from "../../API";
import {setGame, setGameMode, setKey, setPlayer} from "../../actions";
import {useHistory} from "react-router-dom";
import GameModes from "../../GameModes";
import NumberPicker from "../number-picker/NumberPicker";

function NicknameInput() {
    const userId = useSelector(state => state.user);
    if (userId != null) {
        return null;
    }
    return <><Form.Label>Nickname</Form.Label><Form.Control required type="text" placeholder="name" name="nickname"
                                                            data-test="nickname-input"/></>;
}

function CreateGame(props) {
    const [validated, setValidated] = useState(false);
    const dispatch = useDispatch();
    const userId = useSelector(state => state.user);
    const userKey = useSelector(state => state.key);
    const gameMode = useSelector(state => state.gameMode);
    const [numPlayers, setNumPlayers] = useState(GameModes[gameMode] ? GameModes[gameMode].minPlayers : undefined);
    const history = useHistory();

    const onClose = e => {
        props.onClose && props.onClose(e);
        setValidated(false);
    };

    const createGame = (lobbyName, maxPlayers, playerId, playerKey) => {
        let id = (playerId != null) ? playerId : userId;
        let key = (playerKey != null) ? playerKey : userKey;
        API.post('/create', {
            lobbyName: lobbyName,
            host: {
                id: id,
                key: key
            },
            maxPlayers: maxPlayers
        }).then(function (response) {
            dispatch(setGame(response.data.id));
            history.push('/current-game')
        }).catch(function (error) {
            console.log(error);
        });
    }


    const submitHandler = e => {
        e.preventDefault();
        const form = e.currentTarget;
        const valid = form.checkValidity();

        let submitGameMode = e.target.gameMode.value;
        let submitMaxPlayer = e.target.numPlayers.value;
        let validGameMode = Object.keys(GameModes).includes(submitGameMode);

        if (valid === false || !validGameMode) {
            e.stopPropagation();
            setValidated(true);
            return;
        }

        let lobbyName = e.target.lobbyName.value;

        if (userId != null) {
            createGame(lobbyName, submitMaxPlayer);
        } else {
            API.post('/player', {
                username: e.target.nickname.value
            }).then(function (response) {
                dispatch(setPlayer(response.data.id));
                dispatch(setKey(response.data.key));
                createGame(lobbyName, submitMaxPlayer, response.data.id, response.data.key)
            }).catch(function (error) {
                console.log(error);
            });
        }
        dispatch(setGameMode(submitGameMode));
        setValidated(false);
        props.onClose();
    };

    const decrease = () => {
        if (GameModes[gameMode] && numPlayers > GameModes[gameMode].minPlayers) {
            setNumPlayers(numPlayers - 1);
        }
    }

    const increase = () => {
        if (GameModes[gameMode] && numPlayers < GameModes[gameMode].maxPlayers) {
            setNumPlayers(numPlayers + 1);
        }
    }

    const gamesList = Object.keys(GameModes);

    const renderGameMode = (game, index) => {
        if (GameModes[game].enabled) {
            return (
                <option key={index} value={game}>{game}</option>
            );
        } else {
            return (
                <option disabled key={index} value={game}>{game} (not yet available)</option>
            );
        }
    }

    const onGameModeChange = e => {
        dispatch(setGameMode(e.target.value));
        setNumPlayers(GameModes[e.target.value].minPlayers);
    }

    if (props.show) {

        return (
            <div>
                <style type="text/css">
                    {`
                        .modal-header button {
                           display: none;
                        }
                        
                        .btn-xxl {
                            font-size: 1.6rem;
                            border: grey solid 1px;
                            width: 220px;
                            margin: 20px 10px 30px;
                        }
                        
                        .left-title {
                            text-align: left;
                        }
                        
                        @media screen and (max-width: 768px) {
                            .btn-xxl {
                                font-size: 1.3rem;
                                width: 170px;
                            }
                        }
                        
                        .create-game-modal {
                            margin-top: 50px;
                        }
                        
                        input[type=text] {
                            margin-bottom: 5px;
                        }
                    `}
                </style>
                <Modal className="create-game-modal" show={props.show} onHide={props.onClose}>
                    <Modal.Header closeButton>
                        <Modal.Title>Create Game</Modal.Title>
                    </Modal.Header>
                    <Form noValidate validated={validated} onSubmit={submitHandler}>
                        <Modal.Body>
                            <NicknameInput/>
                            <Form.Label>Lobby Name</Form.Label>
                            <Form.Control required type="text" placeholder="Lobby name" name="lobbyName"
                                          data-test="lobby-name-input"/>
                            <Form.Label>Game Mode</Form.Label>
                            <Form.Control required as="select" name="gameMode" defaultValue="Select"
                                          onChange={onGameModeChange} data-test="game-mode-select">
                                <option key={'Select...'} value={''}>Select...</option>
                                {gamesList.map(renderGameMode)}
                            </Form.Control>
                            <Form.Label>Number of players</Form.Label>
                            <NumberPicker value={numPlayers} onIncrease={increase} onDecrease={decrease}
                                          name={"numPlayers"}/>
                        </Modal.Body>
                        <Modal.Footer>
                            <Button variant="secondary" onClick={onClose}
                                    data-test="close-create-game-btn">Close</Button>
                            <Button type="submit" variant="info" data-test="submit-create-game-btn">Create</Button>
                        </Modal.Footer>
                    </Form>
                </Modal>
            </div>
        )
    } else {
        return null;
    }
}

export default CreateGame;