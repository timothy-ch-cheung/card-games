import React, {useEffect, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import API from "../API";
import {useHistory} from "react-router-dom";
import {resetGame} from "../actions";
import PlayerList from "../components/player-list/PlayerList";
import LobbySettings from "../components/lobby-settings/LobbySettings";

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
        setLeaveText("Leave Lobby");
        API.patch(`/leave/${gameId}`, {
            id: userId
        }).then(function (response) {
            dispatch(resetGame());
            history.push('/games/public');
        }).catch(function (error) {
            console.log(error);
        });
    }

    return (
        <div style={{display:"flex"}}>
            <PlayerList players={[{name: getHost(game), isHost: true}, {name: getGuest(game), isHost: false}]}
                        onLeave={onLeaveGame}/>
            <LobbySettings/>
        </div>
    );

}

export default Lobby;