import * as React from "react";
import {useState} from "react";
import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";
import Form from "react-bootstrap/Form";
import {useDispatch, useSelector} from "react-redux";
import API from "../../API";
import {setGame, setPlayer} from "../../actions";
import {useHistory} from "react-router-dom";
import GameModes from "../../GameModes";
import Dropdown from "react-bootstrap/Dropdown";
import {isBlank} from "../../Util";

function NicknameInput() {

    const userId = useSelector(state => state.user);
    if (userId != null) {
        return null;
    }
    return <><Form.Label>Nickname</Form.Label><Form.Control required type="text" placeholder="name"
                                                            name="nickname"/></>;
}

function CreateGame(props) {

    const [validated, setValidated] = useState(false);
    const dispatch = useDispatch();
    const userId = useSelector(state => state.user);
    const history = useHistory();

    const onClose = e => {
        props.onClose && props.onClose(e);
        setValidated(false);
    };

    const createGame = (lobbyName, playerId) => {
        let id = (playerId != null) ? playerId : userId;
        API.post('/create', {
            lobbyName: lobbyName,
            host: {
                id: id
            }
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

        let validGameMode = Object.entries(GameModes).map(([key, val]) => val.name).includes(e.target.gameMode.value);

        if (valid === false || !validGameMode) {
            e.stopPropagation();
            setValidated(true);
            return;
        }

        let lobbyName = e.target.lobbyName.value;

        if (userId != null) {
            createGame(lobbyName);
        } else {
            API.post('/player', {
                username: e.target.nickname.value
            }).then(function (response) {
                dispatch(setPlayer(response.data.id));
                createGame(lobbyName, response.data.id)
            }).catch(function (error) {
                console.log(error);
            });
        }
        setValidated(false);
        props.onClose();
    };

    const gamesList = Object.entries(GameModes).map(([key,val]) => val);

    const renderGameMode = (game, index) => {
        if(game.enabled) {
            return (
                <option key={index}>{game.name}</option>
            );
        }
        else{
            return (
                <option disabled key={index}>{game.name} (not yet available)</option>
            );
        }
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
                            <Form.Control required type="text" placeholder="Lobby name" name="lobbyName"/>
                            <Form.Label>Game Mode</Form.Label>
                            <Form.Control required as="select"  name="gameMode" defaultValue="Select">
                                <option key={'Select...'} value={''}>Select...</option>
                                {gamesList.map(renderGameMode)}
                            </Form.Control>
                        </Modal.Body>
                        <Modal.Footer>
                            <Button variant="secondary" onClick={onClose}>Close</Button>
                            <Button type="submit" variant="info">Create</Button>
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