package com.cheung.tim.server.service;

import com.cheung.tim.server.domain.Game;
import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.GameDTO;
import com.cheung.tim.server.dto.PlayerDTO;
import com.cheung.tim.server.enums.GameStatus;
import com.cheung.tim.server.exception.BadRequestException;
import com.cheung.tim.server.exception.NotFoundException;
import com.cheung.tim.server.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;

import static com.cheung.tim.server.enums.GameStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

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

    @ParameterizedTest
    @EnumSource(GameStatus.class)
    void getGame_shouldReturnGame(GameStatus gameStatus) {
        Player player = new Player("40283481721d879601721d87b6350000", "John Smith");
        when(gameRepository.findByGameId(1L)).thenReturn(new Game("test_lobby", player, gameStatus));
        Game game = gameService.getGame(1L);
        assertThat(game.getLobbyName(), is("test_lobby"));
        verify(gameRepository).findByGameId(1L);
    }

    @Test
    void getGame_shouldThrowNotFoundException() {
        when(gameRepository.findByGameId(1L)).thenReturn(null);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            gameService.getGame(1L);
        });
        assertThat(exception.getMessage(), is("Game with id 1 does not exist"));
        verify(gameRepository).findByGameId(1L);
    }

    @Test
    void findOpenGames_shouldReturnEmptyListIfNoneFound() {
        when(gameRepository.findByGameStatus(OPEN)).thenReturn(new ArrayList<>());
        assertTrue(gameService.findOpenGames().isEmpty());
        verify(gameRepository).findByGameStatus(any());
    }

    @Test
    void findOpenGames_shouldReturnOpenGames() {
        when(gameRepository.findByGameStatus(OPEN)).thenReturn(new ArrayList<>(Arrays.asList(new Game[]{new Game()})));
        assertThat(gameService.findOpenGames().size(), is(1));
        verify(gameRepository).findByGameStatus(any());
    }

    @Test
    void createGame_shouldThrowExceptionIfPlayerDoesNotExist() {
        when(playerService.findPlayerById(any())).thenReturn(null);
        PlayerDTO playerDTO = getPlayerDTO();
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            gameService.createGame(playerDTO, "test lobby");
        });
        assertThat(exception.getMessage(), is("Player with id 40283481721d879601721d87b6350000 not found"));
    }

    @Test
    void createGame_shouldThrowExceptionIfPlayerAlreadyInGameAsHost() {
        when(playerService.findPlayerById(any())).thenReturn(new Player("40283481721d879601721d87b6350000", "John Smith"));
        when(gameRepository.countByPlayerOneInGame(any())).thenReturn(new Long(1));
        PlayerDTO playerDTO = getPlayerDTO();
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.createGame(playerDTO, "test lobby");
        });
        assertThat(exception.getMessage(), is("Player John Smith is already in a game"));
    }

    @Test
    void createGame_shouldThrowExceptionIfPlayerAlreadyInGameAsJoin() {
        when(playerService.findPlayerById(any())).thenReturn(new Player("40283481721d879601721d87b6350000", "John Smith"));
        when(gameRepository.countByPlayerTwoInGame(any())).thenReturn(new Long(1));
        PlayerDTO playerDTO = getPlayerDTO();
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.createGame(playerDTO, "test lobby");
        });
        assertThat(exception.getMessage(), is("Player John Smith is already in a game"));
    }

    @Test
    void createGame_shouldThrowExceptionIfPlayerDTONull() {
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.createGame(null, "test lobby");
        });
        assertThat(exception.getMessage(), is("Lobby name or Host not supplied"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "abcde", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "435465433"})
    void createGame_shouldThrowExceptionIfPlayerDTOIncorrectIdFormat(String playerId) {
        PlayerDTO playerDTO = new PlayerDTO(playerId, "John");
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.createGame(playerDTO, "test lobby");
        });
        assertThat(exception.getMessage(), is("Lobby name or Host not supplied"));
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "   ", "\n", "\t"})
    void createGame_shouldThrowExceptionIfLobbyNameEmpty(String lobbyName) {
        PlayerDTO playerDTO = getPlayerDTO();
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.createGame(playerDTO, lobbyName);
        });
        assertThat(exception.getMessage(), is("Lobby name or Host not supplied"));
    }

    @Test
    void createGame_shouldCreateGameSuccessfully() {
        when(playerService.findPlayerById(any())).thenReturn(new Player());
        when(gameRepository.countByPlayerOneInGame(any())).thenReturn(new Long(0));
        when(gameRepository.countByPlayerTwoInGame(any())).thenReturn(new Long(0));
        when(gameRepository.save(any())).thenReturn(new Game("test lobby", new Player(), OPEN));

        assertDoesNotThrow(() -> {
            gameService.createGame(getPlayerDTO(), "test lobby");
        });
        verify(gameRepository).save(any());
    }

    @Test
    void joinGame_shouldThrowExceptionIfPlayerAlreadyInGameAsHost() {
        Player player = new Player("40283481721d879601721d87b6350000", "John Smith");
        when(playerService.findPlayerById(any())).thenReturn(player);
        when(gameRepository.countByPlayerOneInGame(any())).thenReturn(new Long(1));
        PlayerDTO playerDTO = getPlayerDTO();
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.joinGame(1L, playerDTO);
        });
        assertThat(exception.getMessage(), is("Player John Smith is already in a game"));
    }

    @Test
    void joinGame_shouldThrowExceptionIfGameDoesNotExist() {
        Player player = new Player("40283481721d879601721d87b6350000", "John Smith");
        when(playerService.findPlayerById(any())).thenReturn(player);
        when(gameRepository.countByPlayerOneInGame(any())).thenReturn(new Long(0));
        when(gameRepository.countByPlayerTwoInGame(any())).thenReturn(new Long(0));
        PlayerDTO playerDTO = getPlayerDTO();
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            gameService.joinGame(123456L, playerDTO);
        });
        assertThat(exception.getMessage(), is("Game with id 123456 does not exist"));
    }

    @Test
    void joinGame_shouldThrowExceptionIfPlayerAlreadyInGameAsJoin() {
        Player player = new Player("40283481721d879601721d87b6350000", "John Smith");
        when(playerService.findPlayerById(any())).thenReturn(player);
        when(gameRepository.countByPlayerTwoInGame(any())).thenReturn(new Long(1));
        PlayerDTO playerDTO = getPlayerDTO();
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.joinGame(1L, playerDTO);
        });
        assertThat(exception.getMessage(), is("Player John Smith is already in a game"));
    }

    @Test
    void joinGame_shouldThrowExceptionIfGameAlreadyFull() {
        Player player1 = new Player("9601721d87b635000040283481721d87", "Jane Smith");
        Player player2 = new Player("87b635000049601721d0283481721d87", "Joanne Smith");
        Game game = new Game("test_lobby", player1, OPEN);
        game.setPlayer2(player2);
        Player newPlayer = new Player("40283481721d879601721d87b6350000", "John Smith");
        when(playerService.findPlayerById("40283481721d879601721d87b6350000")).thenReturn(newPlayer);
        when(gameRepository.findByGameId(anyLong())).thenReturn(game);
        PlayerDTO playerDTO = getPlayerDTO();
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.joinGame(1L, playerDTO);
        });
        assertThat(exception.getMessage(), is("Game with id 1 is already full"));
    }

    @Test
    void joinGame_shouldJoinGameSuccessfully() {
        PlayerDTO playerDTO = getPlayerDTO();
        Player player = new Player("40283481721d879601721d87b6350000", "John Smith");
        when(playerService.findPlayerById(any())).thenReturn(player);
        when(gameRepository.countByPlayerOneInGame(any())).thenReturn(new Long(0));
        when(gameRepository.countByPlayerTwoInGame(any())).thenReturn(new Long(0));
        when(gameRepository.findByGameId(1L)).thenReturn(new Game("test_lobby", new Player("Jane Smith"), OPEN));

        assertDoesNotThrow(() -> {
            gameService.joinGame(1L, playerDTO);
        });
        verify(gameRepository).updatePlayerTwo(1L, player);
        verify(gameRepository).updateStatus(1L, READY);
    }

    @Test
    void leaveGame_shouldThrowNotFoundExceptionWhenGameDoesNotExist() {
        PlayerDTO playerDTO = getPlayerDTO();
        when(gameRepository.findByGameId(1L)).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            gameService.leaveGame(1L, playerDTO);
        });
        assertThat(exception.getMessage(), is("Game with id 1 does not exist"));
    }

    @Test
    void leaveGame_shouldThrowNotFoundExceptionWhenDTONull() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            gameService.leaveGame(1L, null);
        });
        assertThat(exception.getMessage(), is("Game with id 1 does not exist"));
    }

    @Test
    void leaveGame_shouldThrowBadRequestPlayerNotInGame() {
        PlayerDTO playerDTO = new PlayerDTO("40283481721d87b63500001721d87960", "Janet Smith");
        Player player1 = new Player("40283481721d879601721d87b6350000", "John Smith");
        Player player2 = new Player("1721d87b635000040283481721d87960", "Jane Smith");
        Game game = new Game("test_lobby", player1, OPEN);
        game.setPlayer2(player2);
        when(gameRepository.findByGameId(1L)).thenReturn(game);
        when(playerService.findPlayerById("40283481721d87b63500001721d87960")).thenReturn(new Player("40283481721d87b63500001721d87960", "Janet Smith"));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.leaveGame(1L, playerDTO);
        });
        assertThat(exception.getMessage(), is("Player Janet Smith is not in game with id 1"));
    }

    @Test
    void leaveGame_shouldThrowNotFoundPlayerDoesNotExist() {
        PlayerDTO playerDTO = new PlayerDTO("00000000000000000000000000000000", "Janet Smith");

        Player player1 = new Player("40283481721d879601721d87b6350000", "John Smith");
        Player player2 = new Player("1721d87b635000040283481721d87960", "Jane Smith");
        Game game = new Game("test_lobby", player1, OPEN);
        game.setPlayer2(player2);
        when(gameRepository.findByGameId(1L)).thenReturn(game);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            gameService.leaveGame(1L, playerDTO);
        });
        assertThat(exception.getMessage(), is("Player with id 00000000000000000000000000000000 not found"));
    }

    @Test
    void leaveGame_shouldDeleteLobbyWhenPlayer1() {
        PlayerDTO playerDTO = getPlayerDTO();
        Player player1 = new Player("40283481721d879601721d87b6350000", "John Smith");
        when(gameRepository.findByGameId(1L)).thenReturn(new Game("test_lobby", player1, OPEN));

        assertDoesNotThrow(() -> {
            gameService.leaveGame(1L, playerDTO);
        });
        verify(gameRepository).updateStatus(1L, DELETED);
        verify(gameRepository).updatePlayerOne(1L, null);
        verify(gameRepository).updatePlayerTwo(1L, null);
    }

    @Test
    void leaveGame_shouldLeaveGameWhenPlayer2() {
        PlayerDTO playerDTO = new PlayerDTO("1721d87b635000040283481721d87960", "Jane Smith");
        Player player1 = new Player("40283481721d879601721d87b6350000", "John Smith");
        Player player2 = new Player("1721d87b635000040283481721d87960", "Jane Smith");
        Game game = new Game("test_lobby", player1, OPEN);
        game.setPlayer2(player2);
        when(gameRepository.findByGameId(1L)).thenReturn(game);

        assertDoesNotThrow(() -> {
            gameService.leaveGame(1L, playerDTO);
        });
        verify(gameRepository).updateStatus(1L, OPEN);
        verify(gameRepository).updatePlayerTwo(1L, null);
        verify(gameRepository, never()).updatePlayerOne(any(), any());
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