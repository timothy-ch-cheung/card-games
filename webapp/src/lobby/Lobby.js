import React, {useEffect, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import API from "../API";
import {useHistory} from "react-router-dom";
import {resetGame, resetGameMode} from "../actions";
import PlayerList from "../components/player-list/PlayerList";
import LobbySettings from "../components/lobby-settings/LobbySettings";

function Lobby(props) {
    const gameId = useSelector(state => state.game);
    const userId = useSelector(state => state.user);
    const userKey = useSelector(state => state.key);
    const gameMode = useSelector(state => state.gameMode);
    const [game, setGame] = useState({});
    const history = useHistory();
    const dispatch = useDispatch();

    const getPlayersList = (game) => {
        let host = game.host || {};
        host.isHost = true;
        return game.guests ? [host].concat(game.guests) : [host];
    }


    useEffect(() => {
        const getGame = () => {
            API.get(`/game/${gameId}`
            ).then(function (response) {
                getPlayersList(response.data);
                setGame(response.data);
            }).catch(function (error) {
                props.onShowError(error.response.data.message);
                history.push("/games/public");
            });
        };

        getGame();
        const interval = setInterval(function () {
            getGame();
        }, 1000);

        return () => {
            clearInterval(interval);
        }
    }, [gameId, history, props]);

    const onLeaveGame = () => {
        API.patch(`/leave/${gameId}`, {
            id: userId,
            key: userKey
        }).then(function (response) {
            dispatch(resetGame());
            dispatch(resetGameMode());
            history.push('/games/public');
        }).catch(function (error) {
            console.log(error);
        });
    }

    return (
        <div style={{display: "flex"}}>
            <PlayerList players={getPlayersList(game)}
                        onLeave={onLeaveGame}/>
            <LobbySettings gameMode={gameMode} numPlayers={2}/>
        </div>
    );

}

export default Lobby;