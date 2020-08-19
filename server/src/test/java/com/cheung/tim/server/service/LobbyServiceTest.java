package com.cheung.tim.server.service;

import com.cheung.tim.server.domain.Lobby;
import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.GameDTO;
import com.cheung.tim.server.dto.PrivatePlayerDTO;
import com.cheung.tim.server.enums.GameStatus;
import com.cheung.tim.server.exception.BadRequestException;
import com.cheung.tim.server.exception.NotFoundException;
import com.cheung.tim.server.repository.LobbyRepository;
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

class LobbyServiceTest {

    @Mock
    PlayerService playerService;

    @Mock
    LobbyRepository lobbyRepository;

    LobbyService lobbyService;
    Player player;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.lobbyService = new LobbyService(lobbyRepository, playerService);
        player = new Player("40283481721d879601721d87b6350000", "John Smith");
    }

    @ParameterizedTest
    @EnumSource(GameStatus.class)
    void getGame_shouldReturnGame(GameStatus gameStatus) {
        when(lobbyRepository.findByGameId(1L)).thenReturn(new Lobby("test_lobby", player, gameStatus, 2));
        Lobby lobby = lobbyService.getGame(1L);
        assertThat(lobby.getLobbyName(), is("test_lobby"));
        verify(lobbyRepository).findByGameId(1L);
    }

    @Test
    void getGame_shouldThrowNotFoundException() {
        when(lobbyRepository.findByGameId(1L)).thenReturn(null);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            lobbyService.getGame(1L);
        });
        assertThat(exception.getMessage(), is("Lobby with id 1 does not exist"));
        verify(lobbyRepository).findByGameId(1L);
    }

    @Test
    void findOpenGames_shouldReturnEmptyListIfNoneFound() {
        when(lobbyRepository.findByGameStatus(OPEN)).thenReturn(new ArrayList<>());
        assertTrue(lobbyService.findOpenGames().isEmpty());
        verify(lobbyRepository).findByGameStatus(any());
    }

    @Test
    void findOpenGames_shouldReturnOpenGames() {
        when(lobbyRepository.findByGameStatus(OPEN)).thenReturn(new ArrayList<>(Arrays.asList(new Lobby[]{new Lobby()})));
        assertThat(lobbyService.findOpenGames().size(), is(1));
        verify(lobbyRepository).findByGameStatus(any());
    }

    @Test
    void createGame_shouldThrowExceptionIfPlayerDoesNotExist() {
        when(playerService.findPlayerById(any())).thenReturn(null);
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            lobbyService.createGame(privatePlayerDTO, "test lobby", 2);
        });
        assertThat(exception.getMessage(), is("Player with id 40283481721d879601721d87b6350000 not found"));
    }

    @Test
    void createGame_shouldThrowExceptionIfPlayerAlreadyInGame() {
        when(playerService.findPlayerById(any())).thenReturn(player);
        when(lobbyRepository.getPlayerInGame(any())).thenReturn(player);
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.createGame(privatePlayerDTO, "test lobby", 2);
        });
        assertThat(exception.getMessage(), is("Player John Smith is already in a game"));
    }

    @Test
    void createGame_shouldThrowExceptionIfPlayerDTONull() {
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.createGame(null, "test lobby", 2);
        });
        assertThat(exception.getMessage(), is("Lobby name or Host not supplied"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "abcde", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "435465433"})
    void createGame_shouldThrowExceptionIfPlayerDTOIncorrectIdFormat(String playerId) {
        PrivatePlayerDTO privatePlayerDTO = new PrivatePlayerDTO(playerId, "John");
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.createGame(privatePlayerDTO, "test lobby", 2);
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
            lobbyService.createGame(privatePlayerDTO, lobbyName, 2);
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
            lobbyService.createGame(privatePlayerDTO, "test lobby", 2);
        });
        assertThat(exception.getMessage(), is("Player id or key invalid"));
    }

    @Test
    void createGame_shouldCreateGameSuccessfully() {
        Player player = new Player();
        player.setKey("keykeykeykeykeykeykeykeykeykeyke");
        when(playerService.findPlayerById(any())).thenReturn(player);
        when(lobbyRepository.getPlayerInGame(any())).thenReturn(null);
        when(lobbyRepository.save(any())).thenReturn(new Lobby("test lobby", new Player(), OPEN, 2));

        assertDoesNotThrow(() -> {
            lobbyService.createGame(getPlayerDTO(), "test lobby", 2);
        });
        verify(lobbyRepository).save(any());
    }

    @Test
    void joinGame_shouldThrowExceptionIfPlayerAlreadyInGameAsHost() {
        when(playerService.findPlayerById(any())).thenReturn(player);
        when(lobbyRepository.getPlayerInGame(any())).thenReturn(player);
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.joinGame(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Player John Smith is already in a game"));
    }

    @Test
    void joinGame_shouldThrowExceptionIfGameDoesNotExist() {
        when(playerService.findPlayerById(any())).thenReturn(player);
        when(lobbyRepository.getPlayerInGame(any())).thenReturn(null);
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            lobbyService.joinGame(123456L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Lobby with id 123456 does not exist"));
    }

    @Test
    void joinGame_shouldThrowExceptionIfPlayerAlreadyInGame() {
        when(playerService.findPlayerById(any())).thenReturn(player);
        when(lobbyRepository.getPlayerInGame(any())).thenReturn(player);
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.joinGame(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Player John Smith is already in a game"));
    }

    @Test
    void joinGame_shouldThrowExceptionIfGameAlreadyFull() {
        Player player1 = new Player("9601721d87b635000040283481721d87", "Jane Smith");
        Player player2 = new Player("87b635000049601721d0283481721d87", "Joanne Smith");
        Player player3 = new Player("9601721d02834887b041721d87635000", "Janet Smith");
        Lobby lobby = new Lobby("test_lobby", player1, OPEN, 3);
        lobby.addGuest(player2);
        lobby.addGuest(player3);
        Player newPlayer = new Player("40283481721d879601721d87b6350000", "John Smith");
        when(playerService.findPlayerById("40283481721d879601721d87b6350000")).thenReturn(newPlayer);
        when(lobbyRepository.findByGameId(anyLong())).thenReturn(lobby);
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.joinGame(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Lobby with id 1 is already full"));
    }

    @Test
    void joinGame_shouldThrowExceptionIfPlayerKeyInvalid() {
        Player player = new Player("40283481721d879601721d87b6350000", "John Smith", "authauthauthauthauthauthauthauth");
        when(playerService.findPlayerById(any())).thenReturn(player);
        when(lobbyRepository.getPlayerInGame(any())).thenReturn(null);
        Player otherPlayer = new Player("40283481721d879601721d87b6350000", "Jane Smith", "keykeykeykeykeykeykeykeykeykeyke");
        when(lobbyRepository.findByGameId(1L)).thenReturn(new Lobby("test_lobby", otherPlayer, OPEN, 2));
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        privatePlayerDTO.setKey("invalidinvalidinvalidinvalidinva");
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.joinGame(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Player id or key invalid"));
    }

    @Test
    void joinGame_shouldJoinGameSuccessfully() {
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        Player player = new Player("40283481721d879601721d87b6350000", "John Smith", "keykeykeykeykeykeykeykeykeykeyke");
        when(playerService.findPlayerById(any())).thenReturn(player);
        when(lobbyRepository.getPlayerInGame(any())).thenReturn(null);
        Player otherPlayer = new Player("40283481721d879601721d87b6350000", "Jane Smith", "keykeykeykeykeykeykeykeykeykeyke");
        when(lobbyRepository.findByGameId(1L)).thenReturn(new Lobby("test_lobby", otherPlayer, OPEN, 2));

        assertDoesNotThrow(() -> {
            lobbyService.joinGame(1L, privatePlayerDTO);
        });
        verify(lobbyRepository).updatePlayersCurrentGame(any(Lobby.class), eq("40283481721d879601721d87b6350000"));
        verify(lobbyRepository).updateStatus(1L, READY);
    }

    @Test
    void leaveGame_shouldThrowNotFoundExceptionWhenGameDoesNotExist() {
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        when(lobbyRepository.findByGameId(1L)).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            lobbyService.leaveGame(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Lobby with id 1 does not exist"));
    }

    @Test
    void leaveGame_shouldThrowNotFoundExceptionWhenDTONull() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            lobbyService.leaveGame(1L, null);
        });
        assertThat(exception.getMessage(), is("Lobby with id 1 does not exist"));
    }

    @Test
    void leaveGame_shouldThrowBadRequestPlayerNotInGame() {
        PrivatePlayerDTO privatePlayerDTO = new PrivatePlayerDTO("40283481721d87b63500001721d87960", "Janet Smith");
        Player player1 = new Player("40283481721d879601721d87b6350000", "John Smith");
        Player player2 = new Player("1721d87b635000040283481721d87960", "Jane Smith");
        Lobby lobby = new Lobby("test_lobby", player1, OPEN, 2);
        lobby.addGuest(player2);
        when(lobbyRepository.findByGameId(1L)).thenReturn(lobby);
        when(playerService.findPlayerById("40283481721d87b63500001721d87960")).thenReturn(new Player("40283481721d87b63500001721d87960", "Janet Smith"));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.leaveGame(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Player Janet Smith is not in lobby with id 1"));
    }

    @Test
    void leaveGame_shouldThrowNotFoundPlayerDoesNotExist() {
        PrivatePlayerDTO privatePlayerDTO = new PrivatePlayerDTO("00000000000000000000000000000000", "Janet Smith");

        Player player1 = new Player("40283481721d879601721d87b6350000", "John Smith");
        Player player2 = new Player("1721d87b635000040283481721d87960", "Jane Smith");
        Lobby lobby = new Lobby("test_lobby", player1, OPEN, 2);
        lobby.addGuest(player2);
        when(lobbyRepository.findByGameId(1L)).thenReturn(lobby);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            lobbyService.leaveGame(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Player with id 00000000000000000000000000000000 not found"));
    }

    @Test
    void leaveGame_shouldThrowBadRequestExceptionWhenKeyInvalidHostKey() {
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        Player player1 = new Player("40283481721d879601721d87b6350000", "John Smith", "keykeykeykeykeykeykeykeykeykeyke");
        when(lobbyRepository.findByGameId(1L)).thenReturn(new Lobby("test_lobby", player1, OPEN, 2));

        privatePlayerDTO.setKey("invalidinvalidinvalidinvalidinva");
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.leaveGame(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Player id or key invalid"));
    }

    @Test
    void leaveGame_shouldThrowBadRequestExceptionWhenKeyInvalidGuestKey() {
        PrivatePlayerDTO privatePlayerDTO = new PrivatePlayerDTO("1721d87b635000040283481721d87960", "Jane Smith", "keykeykeykeykeykeykeykeykeykeyke");
        Player player1 = new Player("40283481721d879601721d87b6350000", "John Smith");
        Player player2 = new Player("1721d87b635000040283481721d87960", "Jane Smith", "keykeykeykeykeykeykeykeykeykeyke");
        Lobby lobby = new Lobby("test_lobby", player1, OPEN, 2);
        lobby.addGuest(player2);
        when(lobbyRepository.findByGameId(1L)).thenReturn(lobby);
        when(playerService.findPlayerById("1721d87b635000040283481721d87960")).thenReturn(player2);

        privatePlayerDTO.setKey("invalidinvalidinvalidinvalidinva");
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.leaveGame(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Player id or key invalid"));
    }

    @Test
    void leaveGame_shouldDeleteLobbyWhenHost() {
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        Player player1 = new Player("40283481721d879601721d87b6350000", "John Smith", "keykeykeykeykeykeykeykeykeykeyke");
        when(lobbyRepository.findByGameId(1L)).thenReturn(new Lobby("test_lobby", player1, OPEN, 2));

        assertDoesNotThrow(() -> {
            lobbyService.leaveGame(1L, privatePlayerDTO);
        });
        verify(lobbyRepository).updateStatus(1L, DELETED);
        verify(lobbyRepository).updateHost(1L, null);
        verify(lobbyRepository).updatePlayersCurrentGame(null, "40283481721d879601721d87b6350000");
    }

    @Test
    void leaveGame_shouldBeAbleToLeaveGameWhenGuest() {
        PrivatePlayerDTO privatePlayerDTO = new PrivatePlayerDTO("1721d87b635000040283481721d87960", "Jane Smith", "keykeykeykeykeykeykeykeykeykeyke");
        Player player1 = new Player("40283481721d879601721d87b6350000", "John Smith");
        Player player2 = new Player("1721d87b635000040283481721d87960", "Jane Smith", "keykeykeykeykeykeykeykeykeykeyke");
        Lobby lobby = new Lobby("test_lobby", player1, OPEN, 2);
        lobby.addGuest(player2);
        when(lobbyRepository.findByGameId(1L)).thenReturn(lobby);
        when(playerService.findPlayerById("1721d87b635000040283481721d87960")).thenReturn(player2);

        assertDoesNotThrow(() -> {
            lobbyService.leaveGame(1L, privatePlayerDTO);
        });
        verify(lobbyRepository).updateStatus(1L, OPEN);
        verify(lobbyRepository).updatePlayersCurrentGame(null, "1721d87b635000040283481721d87960");
        verify(lobbyRepository, never()).updateHost(any(), any());
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