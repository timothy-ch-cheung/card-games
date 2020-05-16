package com.cheung.tim.server.service;

import com.cheung.tim.server.domain.Game;
import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.GameDTO;
import com.cheung.tim.server.dto.PlayerDTO;
import com.cheung.tim.server.exception.BadRequestException;
import com.cheung.tim.server.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;

import static com.cheung.tim.server.enums.GameStatus.OPEN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GameServiceTest {

    @Mock
    PlayerService playerService;

    @Mock
    GameRepository gameRepository;

    GameService gameService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.gameService = new GameService(gameRepository, playerService);
    }

    @Test
    public void findOpenGames_shouldReturnEmptyListIfNoneFound() {
        when(gameRepository.findByGameStatus(OPEN)).thenReturn(new ArrayList<>());
        assertTrue(gameService.findOpenGames().isEmpty());
        verify(gameRepository).findByGameStatus(any());
    }

    @Test
    public void findOpenGames_shouldReturnOpenGames() {
        when(gameRepository.findByGameStatus(OPEN)).thenReturn(new ArrayList<>(Arrays.asList(new Game[]{new Game()})));
        assertThat(gameService.findOpenGames().size(), is(1));
        verify(gameRepository).findByGameStatus(any());
    }

    @Test
    public void createGame_shouldThrowExceptionIfPlayerDoesNotExist() {
        when(playerService.findPlayerById(any())).thenReturn(null);
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.createGame(getGameDTO());
        });
        assertThat(exception.getMessage(), is("Player does not exist"));
    }

    @Test
    public void createGame_shouldThrowExceptionIfPlayerAlreadyInGame() {
        when(playerService.findPlayerById(any())).thenReturn(new Player());
        when(gameRepository.countByPlayer(any())).thenReturn(new Long(1));
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.createGame(getGameDTO());
        });
        assertThat(exception.getMessage(), is("Player John Smith is already in a game"));
    }

    @Test
    public void createGame_shouldCreateGameSuccessfully() {
        GameDTO gameDTO = getGameDTO();
        when(playerService.findPlayerById(any())).thenReturn(new Player());
        when(gameRepository.countByPlayer(any())).thenReturn(new Long(0));
        when(gameRepository.save(any())).thenReturn(new Game(gameDTO.getLobbyName(), new Player(), OPEN));
        gameService.createGame(gameDTO);
        verify(gameRepository).save(any());
    }

    public GameDTO getGameDTO() {
        GameDTO gameDTO = new GameDTO();
        gameDTO.setHost(getPlayerDTO());
        gameDTO.setLobbyName("test lobby");
        return gameDTO;
    }

    public PlayerDTO getPlayerDTO() {
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setId("40283481721d879601721d87b6350000");
        playerDTO.setUsername("John Smith");
        return playerDTO;
    }
}