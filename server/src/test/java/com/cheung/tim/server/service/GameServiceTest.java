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
import static com.cheung.tim.server.enums.GameStatus.READY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    public void createGame_shouldThrowExceptionIfPlayerAlreadyInGameAsHost() {
        when(playerService.findPlayerById(any())).thenReturn(new Player());
        when(gameRepository.countByPlayerOneInGame(any())).thenReturn(new Long(1));
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.createGame(getGameDTO());
        });
        assertThat(exception.getMessage(), is("Player John Smith is already in a game"));
    }

    @Test
    public void createGame_shouldThrowExceptionIfPlayerAlreadyInGameAsJoin() {
        when(playerService.findPlayerById(any())).thenReturn(new Player());
        when(gameRepository.countByPlayerTwoInGame(any())).thenReturn(new Long(1));
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.createGame(getGameDTO());
        });
        assertThat(exception.getMessage(), is("Player John Smith is already in a game"));
    }

    @Test
    public void createGame_shouldCreateGameSuccessfully() {
        GameDTO gameDTO = getGameDTO();
        when(playerService.findPlayerById(any())).thenReturn(new Player());
        when(gameRepository.countByPlayerOneInGame(any())).thenReturn(new Long(0));
        when(gameRepository.countByPlayerTwoInGame(any())).thenReturn(new Long(0));
        when(gameRepository.save(any())).thenReturn(new Game(gameDTO.getLobbyName(), new Player(), OPEN));

        assertDoesNotThrow(() -> {
            gameService.createGame(gameDTO);
        });
        verify(gameRepository).save(any());
    }

    @Test
    public void joinGame_shouldThrowExceptionIfPlayerAlreadyInGameAsHost() {
        Player player = new Player("40283481721d879601721d87b6350000", "John Smith");
        when(playerService.findPlayerById(any())).thenReturn(player);
        when(gameRepository.countByPlayerOneInGame(any())).thenReturn(new Long(1));
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.joinGame(1L, getPlayerDTO());
        });
        assertThat(exception.getMessage(), is("Player John Smith is already in a game"));
    }

    @Test
    public void joinGame_shouldThrowExceptionIfPlayerAlreadyInGameAsJoin() {
        Player player = new Player("40283481721d879601721d87b6350000", "John Smith");
        when(playerService.findPlayerById(any())).thenReturn(player);
        when(gameRepository.countByPlayerTwoInGame(any())).thenReturn(new Long(1));
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.joinGame(1L, getPlayerDTO());
        });
        assertThat(exception.getMessage(), is("Player John Smith is already in a game"));
    }

    @Test
    public void joinGame_shouldThrowExceptionIfGameAlreadyFull() {
        Player player1 = new Player("9601721d87b635000040283481721d87", "Jane Smith");
        Player player2 = new Player("87b635000049601721d0283481721d87", "Joanne Smith");
        Game game = new Game("test_lobby", player1, OPEN);
        game.setPlayer2(player2);
        Player newPlayer = new Player("40283481721d879601721d87b6350000", "John Smith");
        when(playerService.findPlayerById("40283481721d879601721d87b6350000")).thenReturn(newPlayer);
        when(gameRepository.findByGameId(anyLong())).thenReturn(game);
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.joinGame(1L, getPlayerDTO());
        });
        assertThat(exception.getMessage(), is("Game with id 1 is already full"));
    }

    @Test
    public void joinGame_shouldJoinGameSuccessfully() {
        PlayerDTO playerDTO = getPlayerDTO();
        Player player = new Player("40283481721d879601721d87b6350000", "John Smith");
        when(playerService.findPlayerById(any())).thenReturn(player);
        when(gameRepository.countByPlayerOneInGame(any())).thenReturn(new Long(0));
        when(gameRepository.countByPlayerTwoInGame(any())).thenReturn(new Long(0));
        when(gameRepository.findByGameId(1L)).thenReturn(new Game("test_lobby", new Player("Jane Smith"), OPEN));

        assertDoesNotThrow(() -> {
            gameService.joinGame(1L, playerDTO);
        });
        verify(gameRepository).updatePlayerTwoAndStatus(1L, player, READY);
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