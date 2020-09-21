package com.cheung.tim.server.service;

import com.cheung.tim.server.domain.Game;
import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.domain.PlayerGameData;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.spy;

class GameHandlerTest {

    GameHandler gameHandler = spy(GameHandler.class);

    @Test
    void getWinningPlayer_returnsPlayerWithHighestScore() {
        Game game = new Game();
        Set<PlayerGameData> gameData = new HashSet<>();
        PlayerGameData player1 = createPlayerGameData(100, "John");
        PlayerGameData player2 = createPlayerGameData(200, "Jane");

        gameData.add(player1);
        gameData.add(player2);
        game.setPlayerGameData(gameData);

        assertThat(gameHandler.getWinningPlayer(game), is(player2.getPlayer()));
    }

    private PlayerGameData createPlayerGameData(int score, String name) {
        Player player = new Player(name);
        PlayerGameData playerGameData = new PlayerGameData();
        playerGameData.setScore(score);
        playerGameData.setPlayer(player);
        return playerGameData;
    }
}