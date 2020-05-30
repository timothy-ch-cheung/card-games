import * as React from "react";
import {useState} from "react";
import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";
import Form from "react-bootstrap/Form";
import {useDispatch, useSelector} from "react-redux";
import axios from "axios";
import set from "../actions";

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

    const onClose = e => {
        props.onClose && props.onClose(e);
        setValidated(false);
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

        axios.post('http://127.0.0.1:8080/player', {
            username: e.target.nickname.value
        }).then(function (response) {
            console.log(response.data.id);
            dispatch(set(response.data.id));
        }).catch(function (error) {
            console.log(error);
        });

        setValidated(false);
        props.onClose();
    };

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
                            <Form.Control required type="text" placeholder="Lobby name"/>
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