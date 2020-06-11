import Modal from "react-bootstrap/Modal";
import Form from "react-bootstrap/Form";
import Button from "react-bootstrap/Button";
import * as React from "react";
import {useState} from "react";
import {useDispatch, useSelector} from "react-redux";

function CreatePlayer(props) {
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
        props.onSubmit(props.gameId, e.target.nickname.value);
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
            <Modal className="create-player-modal" show={props.show} onHide={props.onClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Set Nickname</Modal.Title>
                </Modal.Header>
                <Form noValidate validated={validated} onSubmit={submitHandler}>
                    <Modal.Body>
                        <Form.Label>Nickname</Form.Label>
                        <Form.Control required type="text" placeholder="name" name="nickname"/>
                    </Modal.Body>

                    <Modal.Footer>
                        <Button variant="secondary" onClick={onClose}>Close</Button>
                        <Button type="submit" variant="info">{props.submitText}</Button>
                    </Modal.Footer>
                </Form>
            </Modal>
        </div>
    )
}

export default CreatePlayer;