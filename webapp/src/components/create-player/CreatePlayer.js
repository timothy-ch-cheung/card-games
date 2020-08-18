import Modal from "react-bootstrap/Modal";
import Form from "react-bootstrap/Form";
import Button from "react-bootstrap/Button";
import * as React from "react";
import {useState} from "react";
import {useDispatch} from "react-redux";
import API from "../../API";
import {setKey, setPlayer} from "../../redux/actions";

function CreatePlayer(props) {

    const dispatch = useDispatch();
    const [validated, setValidated] = useState(false);

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
        setValidated(false);
        props.onClose();
        API.post('/player', {
            username: e.target.nickname.value
        }).then(function (response) {
            console.log(response)
            dispatch(setPlayer(response.data.id));
            dispatch(setKey(response.data.key));
            props.onSubmit(props.gameId, response.data.id, response.data.key);
        }).catch(function (error) {
            console.log(error);
        });
    };

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
                        
                        .create-player-modal {
                            margin-top: 50px;
                        }
                        
                        input[type=text] {
                            margin-bottom: 5px;
                        }
                    `}
            </style>
            <Modal className="create-player-modal" show={props.show} onHide={props.onClose} data-test="create-player-modal">
                <Modal.Header closeButton>
                    <Modal.Title>Set Nickname</Modal.Title>
                </Modal.Header>
                <Form noValidate validated={validated} onSubmit={submitHandler}>
                    <Modal.Body>
                        <Form.Label>Nickname</Form.Label>
                        <Form.Control required type="text" placeholder="name" name="nickname"/>
                    </Modal.Body>

                    <Modal.Footer>
                        <Button variant="secondary" onClick={onClose} data-test="close-create-player-modal">Close</Button>
                        <Button type="submit" variant="info" data-test="submit-create-player-modal">Join Game</Button>
                    </Modal.Footer>
                </Form>
            </Modal>
        </div>
    )
}

export default CreatePlayer;