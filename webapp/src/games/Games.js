import React, {useEffect, useState} from "react";
import LobbyCard from "../components/LobbyCard";
import styled from "styled-components";
import CreateGame from "../components/CreateGame";
import Button from "react-bootstrap/Button";
import API from "../API";

const Divider = styled.div`
border-bottom: 1px solid grey;
margin: 10px;
`;

function Games() {

    const [show, setShow] = useState(false);
    const [games, setGames] = useState([]);

    useEffect(() => {
        const getGames = () => {
            API.get("/games").then(function (response) {
                setGames(response.data.games)
            })
        };

        getGames();
        const interval = setInterval(function () {
            getGames();
        }, 10000)

        return () => {
            clearInterval(interval)
        }
    }, []);

    const handleShow = () => {
        setShow(true);
    }

    const handleHide = () => {
        setShow(false);
    }

    const renderCard = (card, index) => {
        return <LobbyCard lobbyName={card.lobbyName} host={card.host.username} key={index}/>
    }

    return (
        <div>
            <style type="text/css">
                {`
                        .left-title {
                            text-align: left;
                        }
                        
                        .btn-xl {
                            font-size: 1.5rem;
                            border: grey solid 1px;
                            width: 200px;
                            margin: 20px 10px 30px;
                        }
                        
                        .grid {
                             min-height: 100%;
                             display: flex;
                             flex-wrap: wrap;
                             flex-direction: row;
                        }

                    `}
            </style>
            <Button size="xl" variant="info" onClick={e => {
                handleShow();
            }}>Create Game</Button>
            <CreateGame show={show} onClose={handleHide}/>
            <Divider/>
            <h1 className="left-title">Public games</h1>
            <div className="grid">
                {games.map(renderCard)}
            </div>
        </div>
    )
}

export default Games;