package com.cheung.tim.server.service;

import com.cheung.tim.server.domain.Game;
import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.domain.PlayerGameData;

import java.util.Collections;
import java.util.Set;

public interface GameHandler {

    void executeTurn(PlayerGameData playerGameData, Game game);

    boolean isGameEnded(Game game);

    PlayerGameData determineNextPlayer(Game game);

    default Player getWinningPlayer(Game game) {
        Set<PlayerGameData> playerGameData = game.getPlayerGameData();
        PlayerGameData winningPlayer = Collections.max(playerGameData, (s1, s2) -> Integer.compare(s1.getScore(), s2.getScore()));
        return winningPlayer.getPlayer();
    }
}
