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
    const [gameMode, setGameMode] = useState("Game Mode");
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

        let validGameMode = Object.entries(GameModes).map(([key, val]) => val.name).includes(gameMode);

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

    const onSelectGameMode = (eventKey) => {
        setGameMode(eventKey);
    }

    const renderGameMode = (game, index) => {
        return (
            <Dropdown.Item key={index} eventKey={game.name}>{game.name}</Dropdown.Item>
        );
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
                            <Dropdown onSelect={onSelectGameMode}>
                                <Dropdown.Toggle variant="info">
                                    {gameMode}
                                </Dropdown.Toggle>

                                <Dropdown.Menu>
                                    {gamesList.map(renderGameMode)}
                                </Dropdown.Menu>
                            </Dropdown>
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