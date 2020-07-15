import Board from "../components/board/Board";
import React, {useEffect, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import API from "../API";
import Button from "react-bootstrap/Button";
import {useHistory} from "react-router-dom";
import {resetGame} from "../actions";

function getGuest(game) {
    if (game.guest != null) {
        return game.guest.username;
    }
    return "";
}

function getHost(game) {
    if (game.host != null) {
        return game.host.username;
    }
    return "";
}

function Lobby(props) {
    const gameId = useSelector(state => state.game);
    const userId = useSelector(state => state.user);
    const [game, setGame] = useState({});
    const history = useHistory();
    const dispatch = useDispatch();
    const [leaveText, setLeaveText] = useState("Leave Lobby");

    useEffect(() => {
        const getGame = () => {
            API.get(`/game/${gameId}`).then(function (response) {
                setGame(response.data);
            }).catch(function (error) {
                props.onShowError(error.response.data.message)
                history.push("/games")
            });
        };

        getGame();
        const interval = setInterval(function () {
            getGame();
        }, 1000);

        return () => {
            clearInterval(interval);
        }
    }, []);

    const onLeaveGame = () => {
        API.patch(`/leave/${gameId}`, {
            id: userId
        }).then(function (response) {
            dispatch(resetGame());
            history.push('/games')
        }).catch(function (error) {
            console.log(error);
        });
    }

    return (
        <div>
            <style type="text/css">
                {`
                    .lobby-banner {
                        margin: 10px;
                        height: 30px;
                    }
                    
                    .host {
                        display: inline;
                        float: left;
                        min-width: 10%;
                    }
                    
                    .guest {
                        display: inline;
                        float: right;
                        min-width: 10%;
                    }
                    
                    .leave-game {
                        position: absolute;
                        bottom: 0;
                        left: 0;
                        margin: 10px;
                    }
                `}
            </style>
            <div className="lobby-banner">
                <h3 className="host">HOST: {getHost(game)}</h3>
                <h3 className="guest">GUEST: {getGuest(game)}</h3>
            </div>
            <Button variant="info" className="leave-game" onClick={onLeaveGame}>{leaveText}</Button>
        </div>
    );

}

export default Lobby;