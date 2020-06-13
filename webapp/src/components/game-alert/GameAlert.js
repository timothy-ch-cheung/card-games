import React from "react";
import Alert from "react-bootstrap/Alert";
import Fade from "react-bootstrap/cjs/Fade";

function GameAlert(props) {
    const alertStyle = {
        position: "fixed",
        zIndex: 999,
        textAlign: "left",
        top: "10%",
        left: 0,
        right: 0,
        marginLeft: "auto",
        marginRight: "auto",
        padding: "10px"
    }
    if (props.show) {
        return (
            <>
                <style type="text/css">
                    {`
                        .alert-dismissible .close {
                            padding: 5px 10px;
                        }
                        .alert-danger {
                            width: 60%;
                        }
                        @media(max-width: 800px) {
                            .alert-danger {
                                width: 90%;
                            }
                        }
                `}
                </style>
                <Alert variant="danger" style={alertStyle} onClose={() => props.onClose()} dismissible>
                    <Alert.Heading>Error!</Alert.Heading>
                    <p style={{textAlign: "center", marginBottom: "2px"}}>{props.errorText}</p>
                </Alert>
            </>
        )
    }
    return null;
}

export default GameAlert;