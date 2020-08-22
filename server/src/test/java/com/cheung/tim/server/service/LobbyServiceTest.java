package com.cheung.tim.server.service;

import com.cheung.tim.server.domain.Lobby;
import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.CreateLobbyDTO;
import com.cheung.tim.server.dto.PrivatePlayerDTO;
import com.cheung.tim.server.dto.UpdateLobbyDTO;
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

import static com.cheung.tim.server.enums.GameMode.MATCH_TWO;
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
        player = new Player("40283481721d879601721d87b6350000", "John Smith", "keykeykeykeykeykeykeykeykeykeyke");
        when(playerService.findPlayerById(eq("40283481721d879601721d87b6350000"))).thenReturn(player);
    }

    @ParameterizedTest
    @EnumSource(GameStatus.class)
    void getLobby_shouldReturnGame(GameStatus gameStatus) {
        when(lobbyRepository.findByLobbyId(1L)).thenReturn(new Lobby("test_lobby", player, gameStatus, 2, MATCH_TWO));
        Lobby lobby = lobbyService.getLobby(1L);
        assertThat(lobby.getLobbyName(), is("test_lobby"));
        verify(lobbyRepository).findByLobbyId(1L);
    }

    @Test
    void getLobby_shouldThrowNotFoundException() {
        when(lobbyRepository.findByLobbyId(1L)).thenReturn(null);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            lobbyService.getLobby(1L);
        });
        assertThat(exception.getMessage(), is("Lobby with id 1 does not exist"));
        verify(lobbyRepository).findByLobbyId(1L);
    }

    @Test
    void findOpenGames_shouldReturnEmptyListIfNoneFound() {
        when(lobbyRepository.findByLobbyStatus(OPEN)).thenReturn(new ArrayList<>());
        assertTrue(lobbyService.findOpenLobbies().isEmpty());
        verify(lobbyRepository).findByLobbyStatus(any());
    }

    @Test
    void findOpenGames_shouldReturnOpenGames() {
        when(lobbyRepository.findByLobbyStatus(OPEN)).thenReturn(new ArrayList<>(Arrays.asList(new Lobby[]{new Lobby()})));
        assertThat(lobbyService.findOpenLobbies().size(), is(1));
        verify(lobbyRepository).findByLobbyStatus(any());
    }

    @Test
    void createLobby_shouldThrowExceptionIfPlayerDoesNotExist() {
        when(playerService.findPlayerById(any())).thenReturn(null);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            lobbyService.createLobby(getLobbyDTO());
        });
        assertThat(exception.getMessage(), is("Player with id 40283481721d879601721d87b6350000 not found"));
    }

    @Test
    void createLobby_shouldThrowExceptionIfPlayerAlreadyInGame() {
        when(lobbyRepository.getPlayerInLobby(any())).thenReturn(player);
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.createLobby(getLobbyDTO());
        });
        assertThat(exception.getMessage(), is("Player John Smith is already in a game"));
    }

    @Test
    void createLobby_shouldThrowExceptionIfPlayerDTONull() {
        CreateLobbyDTO lobbyDTO = getLobbyDTO();
        lobbyDTO.setHost(null);
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.createLobby(lobbyDTO);
        });
        assertThat(exception.getMessage(), is("Lobby name or Host not supplied"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "abcde", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "435465433"})
    void createLobby_shouldThrowExceptionIfPlayerDTOIncorrectIdFormat(String playerId) {
        PrivatePlayerDTO playerDTO = getPlayerDTO();
        playerDTO.setId(playerId);
        CreateLobbyDTO lobbyDTO = getLobbyDTO();
        lobbyDTO.setHost(playerDTO);
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.createLobby(lobbyDTO);
        });
        assertThat(exception.getMessage(), is("Lobby name or Host not supplied"));
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "   ", "\n", "\t"})
    void createLobby_shouldThrowExceptionIfLobbyNameEmpty(String lobbyName) {
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.createLobby(getLobbyDTO(lobbyName));
        });
        assertThat(exception.getMessage(), is("Lobby name or Host not supplied"));
    }

    @Test
    void createLobby_shouldThrowExceptionIfPlayerKeyIsInvalid() {
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        privatePlayerDTO.setKey("invalidinvalidinvalidinvalidinva");
        CreateLobbyDTO lobbyDTO = getLobbyDTO();
        lobbyDTO.setHost(privatePlayerDTO);
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.createLobby(lobbyDTO);
        });
        assertThat(exception.getMessage(), is("Player id or key invalid"));
    }

    @Test
    void createLobby_shouldCreateGameSuccessfully() {
        when(lobbyRepository.getPlayerInLobby(any())).thenReturn(null);
        when(lobbyRepository.save(any())).thenReturn(new Lobby("test lobby", new Player(), OPEN, 2, MATCH_TWO));

        assertDoesNotThrow(() -> {
            lobbyService.createLobby(getLobbyDTO());
        });
        verify(lobbyRepository).save(any());
    }

    @Test
    void updateLobby_shouldThrowExceptionWhenInvalidKey() {
        when(lobbyRepository.findByLobbyId(1L)).thenReturn(new Lobby("test lobby", player, OPEN, 2, MATCH_TWO));

        UpdateLobbyDTO lobbyDTO = new UpdateLobbyDTO();
        PrivatePlayerDTO playerDTO = getPlayerDTO();
        playerDTO.setKey("invalidinvalidinvalidinvalidinva");
        lobbyDTO.setHost(playerDTO);
        lobbyDTO.setRounds(4);
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.updateLobby(1L, lobbyDTO);
        });
        verify(lobbyRepository, never()).updateRounds(1L, 4);
        assertThat(exception.getMessage(), is("Player id or key invalid"));
    }

    @Test
    void updateLobby_shouldThrowExceptionWhenNullPlayerDTO() {
        UpdateLobbyDTO lobbyDTO = new UpdateLobbyDTO();
        lobbyDTO.setHost(null);
        lobbyDTO.setRounds(4);
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.updateLobby(1L, lobbyDTO);
        });
        verify(lobbyRepository, never()).updateRounds(1L, 4);
        assertThat(exception.getMessage(), is("Lobby name or Host not supplied"));
    }

    @Test
    void updateLobby_shouldThrowExceptionWhenGuestTriesToUpdate() {
        when(lobbyRepository.findByLobbyId(1L)).thenReturn(new Lobby("test lobby", new Player(), OPEN, 2, MATCH_TWO));

        UpdateLobbyDTO lobbyDTO = new UpdateLobbyDTO();
        lobbyDTO.setHost(getPlayerDTO());
        lobbyDTO.setRounds(4);
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.updateLobby(1L, lobbyDTO);
        });
        verify(lobbyRepository, never()).updateRounds(1L, 4);
        assertThat(exception.getMessage(), is("Only host can update lobby"));
    }

    @Test
    void updateLobby_shouldThrowExceptionWhenRoundsInvalidForGameMode() {
        when(lobbyRepository.findByLobbyId(1L)).thenReturn(new Lobby("test lobby", player, OPEN, 2, MATCH_TWO));

        UpdateLobbyDTO lobbyDTO = new UpdateLobbyDTO();
        lobbyDTO.setHost(getPlayerDTO());
        lobbyDTO.setRounds(3);
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.updateLobby(1L, lobbyDTO);
        });
        verify(lobbyRepository, never()).updateRounds(1L, 3);
        assertThat(exception.getMessage(), is("Invalid rounds for game mode"));
    }

    @Test
    void updateLobby_shouldUpdateGameSuccessfully() {
        when(lobbyRepository.findByLobbyId(1L)).thenReturn(new Lobby("test lobby", player, OPEN, 2, MATCH_TWO));

        UpdateLobbyDTO lobbyDTO = new UpdateLobbyDTO();
        lobbyDTO.setHost(getPlayerDTO());
        lobbyDTO.setRounds(4);
        assertDoesNotThrow(() -> {
            lobbyService.updateLobby(1L, lobbyDTO);
        });
        verify(lobbyRepository).updateRounds(1L, 4);
    }

    @Test
    void joinLobby_shouldThrowExceptionIfPlayerAlreadyInGameAsHost() {
        when(lobbyRepository.getPlayerInLobby(any())).thenReturn(player);
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.joinLobby(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Player John Smith is already in a game"));
    }

    @Test
    void joinLobby_shouldThrowExceptionIfGameDoesNotExist() {
        when(lobbyRepository.getPlayerInLobby(any())).thenReturn(null);
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            lobbyService.joinLobby(123456L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Lobby with id 123456 does not exist"));
    }

    @Test
    void joinLobby_shouldThrowExceptionIfPlayerAlreadyInGame() {
        when(lobbyRepository.getPlayerInLobby(any())).thenReturn(player);
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.joinLobby(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Player John Smith is already in a game"));
    }

    @Test
    void joinLobby_shouldThrowExceptionIfGameAlreadyFull() {
        Player player1 = new Player("9601721d87b635000040283481721d87", "Jane Smith");
        Player player2 = new Player("87b635000049601721d0283481721d87", "Joanne Smith");
        Player player3 = new Player("9601721d02834887b041721d87635000", "Janet Smith");
        Lobby lobby = new Lobby("test_lobby", player1, OPEN, 3, MATCH_TWO);
        lobby.addGuest(player2);
        lobby.addGuest(player3);
        Player newPlayer = new Player("40283481721d879601721d87b6350000", "John Smith");
        when(playerService.findPlayerById("40283481721d879601721d87b6350000")).thenReturn(newPlayer);
        when(lobbyRepository.findByLobbyId(anyLong())).thenReturn(lobby);
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.joinLobby(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Lobby with id 1 is already full"));
    }

    @Test
    void joinLobby_shouldThrowExceptionIfPlayerKeyInvalid() {
        Player player = new Player("40283481721d879601721d87b6350000", "John Smith", "authauthauthauthauthauthauthauth");
        when(playerService.findPlayerById(any())).thenReturn(player);
        when(lobbyRepository.getPlayerInLobby(any())).thenReturn(null);
        Player otherPlayer = new Player("40283481721d879601721d87b6350000", "Jane Smith", "keykeykeykeykeykeykeykeykeykeyke");
        when(lobbyRepository.findByLobbyId(1L)).thenReturn(new Lobby("test_lobby", otherPlayer, OPEN, 2, MATCH_TWO));
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        privatePlayerDTO.setKey("invalidinvalidinvalidinvalidinva");
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.joinLobby(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Player id or key invalid"));
    }

    @Test
    void joinLobby_shouldJoinGameSuccessfully() {
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        when(lobbyRepository.getPlayerInLobby(any())).thenReturn(null);
        Player otherPlayer = new Player("40283481721d879601721d87b6350000", "Jane Smith", "keykeykeykeykeykeykeykeykeykeyke");
        when(lobbyRepository.findByLobbyId(1L)).thenReturn(new Lobby("test_lobby", otherPlayer, OPEN, 2, MATCH_TWO));

        assertDoesNotThrow(() -> {
            lobbyService.joinLobby(1L, privatePlayerDTO);
        });
        verify(lobbyRepository).updatePlayersCurrentLobby(any(Lobby.class), eq("40283481721d879601721d87b6350000"));
        verify(lobbyRepository).updateStatus(1L, READY);
    }

    @Test
    void leaveLobby_shouldThrowNotFoundExceptionWhenGameDoesNotExist() {
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        when(lobbyRepository.findByLobbyId(1L)).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            lobbyService.leaveLobby(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Lobby with id 1 does not exist"));
    }

    @Test
    void leaveLobby_shouldThrowNotFoundExceptionWhenDTONull() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            lobbyService.leaveLobby(1L, null);
        });
        assertThat(exception.getMessage(), is("Lobby with id 1 does not exist"));
    }

    @Test
    void leaveLobby_shouldThrowBadRequestPlayerNotInGame() {
        PrivatePlayerDTO privatePlayerDTO = new PrivatePlayerDTO("40283481721d87b63500001721d87960", "Janet Smith");
        Player player1 = new Player("40283481721d879601721d87b6350000", "John Smith");
        Player player2 = new Player("1721d87b635000040283481721d87960", "Jane Smith");
        Lobby lobby = new Lobby("test_lobby", player1, OPEN, 2, MATCH_TWO);
        lobby.addGuest(player2);
        when(lobbyRepository.findByLobbyId(1L)).thenReturn(lobby);
        when(playerService.findPlayerById("40283481721d87b63500001721d87960")).thenReturn(new Player("40283481721d87b63500001721d87960", "Janet Smith"));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.leaveLobby(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Player Janet Smith is not in lobby with id 1"));
    }

    @Test
    void leaveLobby_shouldThrowNotFoundPlayerDoesNotExist() {
        PrivatePlayerDTO privatePlayerDTO = new PrivatePlayerDTO("00000000000000000000000000000000", "Janet Smith");

        Player player1 = new Player("40283481721d879601721d87b6350000", "John Smith");
        Player player2 = new Player("1721d87b635000040283481721d87960", "Jane Smith");
        Lobby lobby = new Lobby("test_lobby", player1, OPEN, 2, MATCH_TWO);
        lobby.addGuest(player2);
        when(lobbyRepository.findByLobbyId(1L)).thenReturn(lobby);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            lobbyService.leaveLobby(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Player with id 00000000000000000000000000000000 not found"));
    }

    @Test
    void leaveLobby_shouldThrowBadRequestExceptionWhenKeyInvalidHostKey() {
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        Player player1 = new Player("40283481721d879601721d87b6350000", "John Smith", "keykeykeykeykeykeykeykeykeykeyke");
        when(lobbyRepository.findByLobbyId(1L)).thenReturn(new Lobby("test_lobby", player1, OPEN, 2, MATCH_TWO));

        privatePlayerDTO.setKey("invalidinvalidinvalidinvalidinva");
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.leaveLobby(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Player id or key invalid"));
    }

    @Test
    void leaveLobby_shouldThrowBadRequestExceptionWhenKeyInvalidGuestKey() {
        PrivatePlayerDTO privatePlayerDTO = new PrivatePlayerDTO("1721d87b635000040283481721d87960", "Jane Smith", "keykeykeykeykeykeykeykeykeykeyke");
        Player player1 = new Player("40283481721d879601721d87b6350000", "John Smith");
        Player player2 = new Player("1721d87b635000040283481721d87960", "Jane Smith", "keykeykeykeykeykeykeykeykeykeyke");
        Lobby lobby = new Lobby("test_lobby", player1, OPEN, 2, MATCH_TWO);
        lobby.addGuest(player2);
        when(lobbyRepository.findByLobbyId(1L)).thenReturn(lobby);
        when(playerService.findPlayerById("1721d87b635000040283481721d87960")).thenReturn(player2);

        privatePlayerDTO.setKey("invalidinvalidinvalidinvalidinva");
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            lobbyService.leaveLobby(1L, privatePlayerDTO);
        });
        assertThat(exception.getMessage(), is("Player id or key invalid"));
    }

    @Test
    void leaveLobby_shouldDeleteLobbyWhenHost() {
        PrivatePlayerDTO privatePlayerDTO = getPlayerDTO();
        Player player1 = new Player("40283481721d879601721d87b6350000", "John Smith", "keykeykeykeykeykeykeykeykeykeyke");
        when(lobbyRepository.findByLobbyId(1L)).thenReturn(new Lobby("test_lobby", player1, OPEN, 2, MATCH_TWO));

        assertDoesNotThrow(() -> {
            lobbyService.leaveLobby(1L, privatePlayerDTO);
        });
        verify(lobbyRepository).updateStatus(1L, DELETED);
        verify(lobbyRepository).updateHost(1L, null);
        verify(lobbyRepository).updatePlayersCurrentLobby(null, "40283481721d879601721d87b6350000");
    }

    @Test
    void leaveLobby_shouldBeAbleToLeaveGameWhenGuest() {
        PrivatePlayerDTO privatePlayerDTO = new PrivatePlayerDTO("1721d87b635000040283481721d87960", "Jane Smith", "keykeykeykeykeykeykeykeykeykeyke");
        Player player1 = new Player("40283481721d879601721d87b6350000", "John Smith");
        Player player2 = new Player("1721d87b635000040283481721d87960", "Jane Smith", "keykeykeykeykeykeykeykeykeykeyke");
        Lobby lobby = new Lobby("test_lobby", player1, OPEN, 2, MATCH_TWO);
        lobby.addGuest(player2);
        when(lobbyRepository.findByLobbyId(1L)).thenReturn(lobby);
        when(playerService.findPlayerById("1721d87b635000040283481721d87960")).thenReturn(player2);

        assertDoesNotThrow(() -> {
            lobbyService.leaveLobby(1L, privatePlayerDTO);
        });
        verify(lobbyRepository).updateStatus(1L, OPEN);
        verify(lobbyRepository).updatePlayersCurrentLobby(null, "1721d87b635000040283481721d87960");
        verify(lobbyRepository, never()).updateHost(any(), any());
    }

    public PrivatePlayerDTO getPlayerDTO() {
        PrivatePlayerDTO privatePlayerDTO = new PrivatePlayerDTO();
        privatePlayerDTO.setId("40283481721d879601721d87b6350000");
        privatePlayerDTO.setUsername("John Smith");
        privatePlayerDTO.setKey("keykeykeykeykeykeykeykeykeykeyke");
        return privatePlayerDTO;
    }

    private CreateLobbyDTO getLobbyDTO(String lobbyName) {
        CreateLobbyDTO lobbyDTO = new CreateLobbyDTO();
        lobbyDTO.setHost(getPlayerDTO());
        lobbyDTO.setLobbyName(lobbyName);
        lobbyDTO.setMaxPlayers(2);
        lobbyDTO.setGameMode("MATCH_TWO");
        return lobbyDTO;
    }

    private CreateLobbyDTO getLobbyDTO() {
        return getLobbyDTO("test_lobby");
    }
}