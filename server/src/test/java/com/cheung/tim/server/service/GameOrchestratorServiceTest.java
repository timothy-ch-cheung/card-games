package com.cheung.tim.server.service;

import com.cheung.tim.server.domain.Game;
import com.cheung.tim.server.domain.Lobby;
import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.domain.PlayerGameData;
import com.cheung.tim.server.repository.GameRepository;
import com.cheung.tim.server.repository.LobbyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.cheung.tim.server.enums.GameMode.MATCH_TWO;
import static com.cheung.tim.server.enums.GameStatus.OPEN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GameOrchestratorServiceTest {

    @Mock
    GameHandler gameHandler;

    @Mock
    GameRepository gameRepository;

    @Mock
    LobbyRepository lobbyRepository;

    GameOrchestratorService gameOrchestratorService;
    Lobby lobby;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        gameOrchestratorService = new GameOrchestratorService(lobbyRepository, gameRepository);
        lobby = createLobby();
        when(lobbyRepository.findByLobbyId(1L)).thenReturn(lobby);
        when(gameHandler.determineNextPlayer(any(Game.class))).thenReturn(new PlayerGameData());
    }

    @Test
    void handleTurn_gameEnded() {
        Player winningPlayer = new Player("123", "John");

        when(gameHandler.isGameEnded(any(Game.class))).thenReturn(true);
        when(gameHandler.getWinningPlayer(any(Game.class))).thenReturn(winningPlayer);

        gameOrchestratorService.handleTurn(1L, gameHandler);
        assertThat(lobby.getGame().getWinner(), is(winningPlayer));
        verify(gameRepository).save(any());
    }

    @Test
    void handleTurn_gameNotEnded() {
        when(gameHandler.isGameEnded(any(Game.class))).thenReturn(false);

        gameOrchestratorService.handleTurn(1L, gameHandler);
        verify(gameHandler).executeTurn(any(), any());
        verify(gameRepository).save(any());
    }

    private Lobby createLobby() {
        Lobby lobby = new Lobby("test_lobby", new Player(), OPEN, 2, MATCH_TWO);
        lobby.setGame(new Game());
        return lobby;
    }
}