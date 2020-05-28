import * as React from "react";
import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";
import Form from "react-bootstrap/Form";
import {useSelector} from "react-redux";

function NicknameInput() {
    const userId = useSelector(state => state.user);
    if (userId != null) {
        return null;
    }
    return <><Form.Label>Nickname</Form.Label><Form.Control type="text" placeholder="name"/></>;
}

class CreateGame extends React.Component {

    onClose = e => {
        this.props.onClose && this.props.onClose(e);
    };

    submitHandler = e => {
        e.preventDefault();
        this.onClose(e)
        console.log("submitted");
    };

    render() {
        if (!this.props.show) {
            return null;
        }

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
                <Modal className="create-game-modal" show={this.props.show} onHide={this.props.onClose}>
                    <Modal.Header closeButton>
                        <Modal.Title>Create Game</Modal.Title>
                    </Modal.Header>
                    <Form onSubmit={this.submitHandler}>
                        <Modal.Body>
                            <NicknameInput/>
                            <Form.Label>Lobby Name</Form.Label>
                            <Form.Control type="text" placeholder="Lobby name"/>
                        </Modal.Body>

                        <Modal.Footer>
                            <Button variant="secondary" onClick={this.onClose}>Close</Button>
                            <Button type="submit" variant="info">Create</Button>
                        </Modal.Footer>
                    </Form>
                </Modal>
            </div>
        )
    }
}

export default CreateGame;