import React, {useEffect, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import API, {baseURL} from "../API";
import {useHistory} from "react-router-dom";
import {resetGame, resetGameMode, setGameMode} from "../redux/actions";
import PlayerList from "../components/player-list/PlayerList";
import LobbySettings from "../components/lobby-settings/LobbySettings";
import SockJsClient from 'react-stomp';
import {isEmpty} from "lodash";

function Lobby(props) {
    const gameId = useSelector(state => state.game);
    const userId = useSelector(state => state.user);
    const userKey = useSelector(state => state.key);
    const gameMode = useSelector(state => state.gameMode);
    const [game, setGame] = useState({});
    const history = useHistory();
    const dispatch = useDispatch();

    const isGameHost = () => {
        if (!game.host || !userId) {
            return undefined;
        }
        return game.host.id === userId
    }

    const getPlayersList = () => {
        let host = game.host || {};
        host.isHost = true;
        return game.guests ? [host].concat(game.guests) : [host];
    }


    useEffect(() => {
        API.get(`/game/${gameId}`
        ).then(function (response) {
            if (response.data.gameStatus === 'DELETED') {
                dispatch(resetGame());
            } else {
                setGame(response.data);
                dispatch(setGameMode(response.data.gameMode));
            }
        }).catch(function (error) {
            props.onShowError(error.response.data.message);
            dispatch(resetGame());
        });
    }, [gameId, props]);

    useEffect(() => {
        if (gameId == null) {
            history.push('/games/public')
        }
    }, [gameId, history])

    const onLobbyUpdate = gameData => {
        if (isEmpty(gameData) || gameData == null || gameData.gameStatus === 'DELETED') {
            dispatch(resetGame());
        } else {
            setGame(gameData);
        }
    }

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
            <SockJsClient url={baseURL + `/websocket`} topics={[`/topic/game/${gameId}`]} onMessage={onLobbyUpdate}/>
            <PlayerList players={getPlayersList()} onLeave={onLeaveGame} maxPlayers={game.maxPlayers}/>
            <LobbySettings gameMode={gameMode} numPlayers={getPlayersList().length} isHost={isGameHost()}
                           rounds={game.rounds || 1} userId={userId} userKey={userKey} gameId={gameId}/>
        </div>
    );

}

export default Lobby;