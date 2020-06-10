import Board from "../components/board/Board";
import React, {useEffect, useState} from "react";
import {useSelector} from "react-redux";
import API from "../API";

function getGuest (game) {
    if (game.guest != null) {
        return game.guest.username;
    }
    return "";
}

function getHost (game) {
    if (game.host != null) {
        return game.host.username;
    }
    return "";
}

function Lobby() {
    const gameId = useSelector(state => state.game);
    const [game, setGame] = useState({});

    useEffect(() => {
        const getGame = () => {
            API.get(`/game/${gameId}`).then(function (response) {
                setGame(response.data);
            })
        };

        getGame();
        const interval = setInterval(function () {
            getGame();
        }, 1000);

        return () => {
            clearInterval(interval);
        }
    }, []);

    return (
        <div>
            <style type="text/css">
                {`
                    .lobby-banner {
                        margin: 10px;
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
                `}
            </style>
            <div className="lobby-banner">
                <h3 className="host">HOST: {getHost(game)}</h3>
                <h3 className="guest">GUEST: {getGuest(game)}</h3>
            </div>
            <Board/>
        </div>
    );

}

export default Lobby;