import React, {useEffect, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import API from "../API";
import {useHistory} from "react-router-dom";
import {resetGame, resetGameMode, setGameMode} from "../redux/actions";
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

    const getPlayersList = () => {
        let host = game.host || {};
        host.isHost = true;
        return game.guests ? [host].concat(game.guests) : [host];
    }

    useEffect(() => {
        const getGame = () => {
            API.get(`/game/${gameId}`
            ).then(function (response) {
                if (response.data.gameStatus === 'DELETED') {
                    dispatch(resetGame());
                    clearInterval(interval);
                    history.push("/games/public");
                }
                setGame(response.data);
                setGameMode(response.data.gameMode);
            }).catch(function (error) {
                props.onShowError(error.response.data.message);
                clearInterval(interval);
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
    }, [gameId, history, props, dispatch]);

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
            <PlayerList players={getPlayersList()} onLeave={onLeaveGame} numPlayers={2} maxPlayers={game.maxPlayers}/>
            <LobbySettings gameMode={gameMode} numPlayers={2}/>
        </div>
    );

}

export default Lobby;