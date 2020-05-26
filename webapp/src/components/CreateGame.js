import * as React from "react";
import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";

class CreateGame extends React.Component {

    onClose = e => {
        this.props.onClose && this.props.onClose(e);
    };

    render() {
        if(!this.props.show){
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
                    `}
                </style>
                <Modal className="create-game-modal" show={this.props.show} onHide={this.props.onClose}>
                    <Modal.Header closeButton>
                        <Modal.Title>Lobby Name</Modal.Title>
                    </Modal.Header>

                    <Modal.Body>
                        <p>Modal body text goes here.</p>
                    </Modal.Body>

                    <Modal.Footer>
                        <Button variant="secondary" onClick={this.onClose}>Close</Button>
                        <Button variant="info">Create</Button>
                    </Modal.Footer>
                </Modal>
            </div>
        )
    }
}

export default CreateGame;