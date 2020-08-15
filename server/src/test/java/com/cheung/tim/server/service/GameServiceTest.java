package com.cheung.tim.server.service;

import com.cheung.tim.server.domain.Game;
import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.GameDTO;
import com.cheung.tim.server.dto.PrivatePlayerDTO;
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
    Player player;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.gameService = new GameService(gameRepository, playerService);
        player = new Player("40283481721d879601721d87b6350000", "John Smith");
    }

    @ParameterizedTest
    @EnumSource(GameStatus.class)
    void getGame_shouldReturnGame(GameStatus gameStatus) {
        when(gameRepository.findByGameId(1L)).thenReturn(new Game("test_lobby", player, gameStatus, 2));
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
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            gameService.createGame(privatePlayerDTO, "test lobby", 2);
        });
        assertThat(exception.getMessage(), is("Player with id 40283481721d879601721d87b6350000 not found"));
    }

    @Test
    void createGame_shouldThrowExceptionIfPlayerAlreadyInGame() {
        when(playerService.findPlayerById(any())).thenReturn(player);
        when(gameRepository.getPlayerInGame(any())).thenReturn(player);
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.createGame(privatePlayerDTO, "test lobby", 2);
        });
        assertThat(exception.getMessage(), is("Player John Smith is already in a game"));
    }

    @Test
    void createGame_shouldThrowExceptionIfPlayerDTONull() {
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.createGame(null, "test lobby", 2);
        });
        assertThat(exception.getMessage(), is("Lobby name or Host not supplied"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "abcde", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "435465433"})
    void createGame_shouldThrowExceptionIfPlayerDTOIncorrectIdFormat(String playerId) {
        PrivatePlayerDTO privatePlayerDTO = new PrivatePlayerDTO(playerId, "John");
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.createGame(privatePlayerDTO, "test lobby", 2);
        });
        assertThat(exception.getMessage(), is("Lobby name or Host not supplied"));
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "   ", "\n", "\t"})
    void createGame_shouldThrowExceptionIfLobbyNameEmpty(String lobbyName) {
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.createGame(privatePlayerDTO, lobbyName, 2);
        });
        assertThat(exception.getMessage(), is("Lobby name or Host not supplied"));
    }

    @Test
    void createGame_shouldThrowExceptionIfPlayerKeyIsInvalid() {
        when(playerService.findPlayerById(any()))
                .thenReturn(new Player("40283481721d879601721d87b6350000", "John Smith", "keykeykeykeykeykeykeykeykeykeyke"));
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        privatePlayerDTO.setKey("invalidinvalidinvalidinvalidinva");
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.createGame(privatePlayerDTO, "test lobby", 2);
        });
        assertThat(exception.getMessage(), is("Player id or key invalid"));
    }

    @Test
    void createGame_shouldCreateGameSuccessfully() {
        Player player = new Player();
        player.setKey("keykeykeykeykeykeykeykeykeykeyke");
        when(playerService.findPlayerById(any())).thenReturn(player);
        when(gameRepository.getPlayerInGame(any())).thenReturn(null);
        when(gameRepository.save(any())).thenReturn(new Game("test lobby", new Player(), OPEN, 2));

        assertDoesNotThrow(() -> {
            gameService.createGame(getPlayerDTO(), "test lobby", 2);
        });
        verify(gameRepository).save(any());
    }

    @Test
    void joinGame_shouldThrowExceptionIfPlayerAlreadyInGameAsHost() {
        when(playerService.findPlayerById(any())).thenReturn(player);
        when(gameRepository.getPlayerInGame(any())).thenReturn(player);
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.joinGame(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Player John Smith is already in a game"));
    }

    @Test
    void joinGame_shouldThrowExceptionIfGameDoesNotExist() {
        when(playerService.findPlayerById(any())).thenReturn(player);
        when(gameRepository.getPlayerInGame(any())).thenReturn(null);
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            gameService.joinGame(123456L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Game with id 123456 does not exist"));
    }

    @Test
    void joinGame_shouldThrowExceptionIfPlayerAlreadyInGame() {
        when(playerService.findPlayerById(any())).thenReturn(player);
        when(gameRepository.getPlayerInGame(any())).thenReturn(player);
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.joinGame(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Player John Smith is already in a game"));
    }

    @Test
    void joinGame_shouldThrowExceptionIfGameAlreadyFull() {
        Player player1 = new Player("9601721d87b635000040283481721d87", "Jane Smith");
        Player player2 = new Player("87b635000049601721d0283481721d87", "Joanne Smith");
        Player player3 = new Player("9601721d02834887b041721d87635000", "Janet Smith");
        Game game = new Game("test_lobby", player1, OPEN, 3);
        game.addGuest(player2);
        game.addGuest(player3);
        Player newPlayer = new Player("40283481721d879601721d87b6350000", "John Smith");
        when(playerService.findPlayerById("40283481721d879601721d87b6350000")).thenReturn(newPlayer);
        when(gameRepository.findByGameId(anyLong())).thenReturn(game);
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.joinGame(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Game with id 1 is already full"));
    }

    @Test
    void joinGame_shouldThrowExceptionIfPlayerKeyInvalid() {
        Player player = new Player("40283481721d879601721d87b6350000", "John Smith", "authauthauthauthauthauthauthauth");
        when(playerService.findPlayerById(any())).thenReturn(player);
        when(gameRepository.getPlayerInGame(any())).thenReturn(null);
        Player otherPlayer = new Player("40283481721d879601721d87b6350000", "Jane Smith", "keykeykeykeykeykeykeykeykeykeyke");
        when(gameRepository.findByGameId(1L)).thenReturn(new Game("test_lobby", otherPlayer, OPEN, 2));
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        privatePlayerDTO.setKey("invalidinvalidinvalidinvalidinva");
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.joinGame(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Player id or key invalid"));
    }

    @Test
    void joinGame_shouldJoinGameSuccessfully() {
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        Player player = new Player("40283481721d879601721d87b6350000", "John Smith", "keykeykeykeykeykeykeykeykeykeyke");
        when(playerService.findPlayerById(any())).thenReturn(player);
        when(gameRepository.getPlayerInGame(any())).thenReturn(null);
        Player otherPlayer = new Player("40283481721d879601721d87b6350000", "Jane Smith", "keykeykeykeykeykeykeykeykeykeyke");
        when(gameRepository.findByGameId(1L)).thenReturn(new Game("test_lobby", otherPlayer, OPEN, 2));

        assertDoesNotThrow(() -> {
            gameService.joinGame(1L, privatePlayerDTO);
        });
        verify(gameRepository).updatePlayersCurrentGame(any(Game.class), eq("40283481721d879601721d87b6350000"));
        verify(gameRepository).updateStatus(1L, READY);
    }

    @Test
    void leaveGame_shouldThrowNotFoundExceptionWhenGameDoesNotExist() {
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        when(gameRepository.findByGameId(1L)).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            gameService.leaveGame(1L, privatePlayerDTO);
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
        PrivatePlayerDTO privatePlayerDTO = new PrivatePlayerDTO("40283481721d87b63500001721d87960", "Janet Smith");
        Player player1 = new Player("40283481721d879601721d87b6350000", "John Smith");
        Player player2 = new Player("1721d87b635000040283481721d87960", "Jane Smith");
        Game game = new Game("test_lobby", player1, OPEN, 2);
        game.addGuest(player2);
        when(gameRepository.findByGameId(1L)).thenReturn(game);
        when(playerService.findPlayerById("40283481721d87b63500001721d87960")).thenReturn(new Player("40283481721d87b63500001721d87960", "Janet Smith"));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.leaveGame(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Player Janet Smith is not in game with id 1"));
    }

    @Test
    void leaveGame_shouldThrowNotFoundPlayerDoesNotExist() {
        PrivatePlayerDTO privatePlayerDTO = new PrivatePlayerDTO("00000000000000000000000000000000", "Janet Smith");

        Player player1 = new Player("40283481721d879601721d87b6350000", "John Smith");
        Player player2 = new Player("1721d87b635000040283481721d87960", "Jane Smith");
        Game game = new Game("test_lobby", player1, OPEN, 2);
        game.addGuest(player2);
        when(gameRepository.findByGameId(1L)).thenReturn(game);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            gameService.leaveGame(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Player with id 00000000000000000000000000000000 not found"));
    }

    @Test
    void leaveGame_shouldThrowBadRequestExceptionWhenKeyInvalidHostKey() {
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        Player player1 = new Player("40283481721d879601721d87b6350000", "John Smith", "keykeykeykeykeykeykeykeykeykeyke");
        when(gameRepository.findByGameId(1L)).thenReturn(new Game("test_lobby", player1, OPEN, 2));

        privatePlayerDTO.setKey("invalidinvalidinvalidinvalidinva");
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.leaveGame(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Player id or key invalid"));
    }

    @Test
    void leaveGame_shouldThrowBadRequestExceptionWhenKeyInvalidGuestKey() {
        PrivatePlayerDTO privatePlayerDTO = new PrivatePlayerDTO("1721d87b635000040283481721d87960", "Jane Smith", "keykeykeykeykeykeykeykeykeykeyke");
        Player player1 = new Player("40283481721d879601721d87b6350000", "John Smith");
        Player player2 = new Player("1721d87b635000040283481721d87960", "Jane Smith", "keykeykeykeykeykeykeykeykeykeyke");
        Game game = new Game("test_lobby", player1, OPEN, 2);
        game.addGuest(player2);
        when(gameRepository.findByGameId(1L)).thenReturn(game);
        when(playerService.findPlayerById("1721d87b635000040283481721d87960")).thenReturn(player2);

        privatePlayerDTO.setKey("invalidinvalidinvalidinvalidinva");
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.leaveGame(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Player id or key invalid"));
    }

    @Test
    void leaveGame_shouldDeleteLobbyWhenHost() {
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        Player player1 = new Player("40283481721d879601721d87b6350000", "John Smith", "keykeykeykeykeykeykeykeykeykeyke");
        when(gameRepository.findByGameId(1L)).thenReturn(new Game("test_lobby", player1, OPEN, 2));

        assertDoesNotThrow(() -> {
            gameService.leaveGame(1L, privatePlayerDTO);
        });
        verify(gameRepository).updateStatus(1L, DELETED);
        verify(gameRepository).updateHost(1L, null);
        verify(gameRepository).updatePlayersCurrentGame(null, "40283481721d879601721d87b6350000");
    }

    @Test
    void leaveGame_shouldBeAbleToLeaveGameWhenGuest() {
        PrivatePlayerDTO privatePlayerDTO = new PrivatePlayerDTO("1721d87b635000040283481721d87960", "Jane Smith", "keykeykeykeykeykeykeykeykeykeyke");
        Player player1 = new Player("40283481721d879601721d87b6350000", "John Smith");
        Player player2 = new Player("1721d87b635000040283481721d87960", "Jane Smith", "keykeykeykeykeykeykeykeykeykeyke");
        Game game = new Game("test_lobby", player1, OPEN, 2);
        game.addGuest(player2);
        when(gameRepository.findByGameId(1L)).thenReturn(game);
        when(playerService.findPlayerById("1721d87b635000040283481721d87960")).thenReturn(player2);

        assertDoesNotThrow(() -> {
            gameService.leaveGame(1L, privatePlayerDTO);
        });
        verify(gameRepository).updateStatus(1L, OPEN);
        verify(gameRepository).updatePlayersCurrentGame(null, "1721d87b635000040283481721d87960");
        verify(gameRepository, never()).updateHost(any(), any());
    }

    public GameDTO getGameDTO() {
        GameDTO gameDTO = new GameDTO();
        gameDTO.setHost(getPlayerDTO());
        gameDTO.setLobbyName("test lobby");
        return gameDTO;
    }

    public PrivatePlayerDTO getPlayerDTO() {
        PrivatePlayerDTO privatePlayerDTO = new PrivatePlayerDTO();
        privatePlayerDTO.setId("40283481721d879601721d87b6350000");
        privatePlayerDTO.setUsername("John Smith");
        privatePlayerDTO.setKey("keykeykeykeykeykeykeykeykeykeyke");
        return privatePlayerDTO;
    }
}