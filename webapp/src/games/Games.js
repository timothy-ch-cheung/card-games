import React, {useEffect, useState} from "react";
import LobbyCard from "../components/lobby-card/LobbyCard";
import styled from "styled-components";
import CreateGame from "../components/create-game/CreateGame";
import Button from "react-bootstrap/Button";
import API from "../API";
import CreatePlayer from "../components/create-player/CreatePlayer";
import {useDispatch} from "react-redux";
import {setGame} from "../actions";
import {useHistory} from "react-router-dom";
import '../App.css';
import Spinner from "react-bootstrap/Spinner";

const Divider = styled.div`
border-bottom: 1px solid grey;
margin: 10px;
`;

function Games() {
    const dispatch = useDispatch();
    const history = useHistory();
    const [showCreateGameModal, setShowCreateGameModal] = useState(false);
    const [showCreatePlayerModal, setShowCreatePlayerModal] = useState(false);
    const [showRefreshSpinner, setShowRefreshSpinner] = useState(false);
    const [currentGameId, setCurrentGameId] = useState(null);
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

    const joinGame = (gameId, userId) => {
        API.patch(`/join/${gameId}`, {
            id: userId
        }).then(function (response) {
            dispatch(setGame(gameId));
            history.push('/current-game')
        }).catch(function (error) {
            console.log(error);
        });
    }

    const handleShowCreateGameModal = () => {
        setShowCreateGameModal(true);
    }

    const handleHideCreateGameModal = () => {
        setShowCreateGameModal(false);
    }

    const handleShowCreatePlayerModal = (gameId) => {
        setCurrentGameId(gameId);
        setShowCreatePlayerModal(true);
    }

    const handleHideCreatePlayerModal = () => {
        setShowCreatePlayerModal(false);
    }

    const handleClickRefresh = () => {
        let startTime = new Date();
        setShowRefreshSpinner(true);
        API.get("/games").then(function (response) {
            setGames(response.data.games);
        }).then(function () {
            let currentTime = new Date();
            let timeElapsed = currentTime - startTime;
            if(timeElapsed > 500) {
                setShowRefreshSpinner(false);
            }
            else {
                setTimeout(function() {setShowRefreshSpinner(false);}, 500 - timeElapsed);
            }
        })
    }

    const renderCard = (card, index) => {
        return <LobbyCard gameId={card.id} lobbyName={card.lobbyName} host={card.host.username} key={index}
                          onSubmit={joinGame} showModal={handleShowCreatePlayerModal}/>
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
                        
                        .container {
                            max-width: 98%;
                            margin: 0 auto;
                        }
                        
                        .row {
                            display: flex;
                            flex-flow: row wrap;
                        }

                    `}
            </style>
            <Button size="xl" variant="info" onClick={e => {
                handleShowCreateGameModal();
            }}>Create Game</Button>
            <CreateGame show={showCreateGameModal} onClose={handleHideCreateGameModal}/>
            <CreatePlayer show={showCreatePlayerModal} onClose={handleHideCreatePlayerModal}
                          onSubmit={joinGame} gameId={currentGameId}/>
            <Divider/>
            <div className="banner">
                <h1 className="left-title" style={{marginLeft: "10px"}}>Public games</h1>
                <Button variant="info" style={{marginRight: "10px", height: "40px", width: "100px", textAlign: "left"}}
                        onClick={handleClickRefresh} data-test="refresh-btn">
                    Refresh{" "}
                    {showRefreshSpinner && <Spinner
                        as="span"
                        animation="border"
                        size="sm"
                        role="status"
                        aria-hidden="true"
                        style={{marginBottom: "2px"}}
                    />}</Button>
            </div>
            <div className="container" data-test="lobby-cards-container">
                <div className="row">
                    {games.map(renderCard)}
                </div>
            </div>
        </div>
    )
}

export default Games;