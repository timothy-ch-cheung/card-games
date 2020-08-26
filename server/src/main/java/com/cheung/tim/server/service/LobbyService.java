package com.cheung.tim.server.service;

import com.cheung.tim.server.domain.Lobby;
import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.CreateLobbyDTO;
import com.cheung.tim.server.dto.PrivatePlayerDTO;
import com.cheung.tim.server.dto.UpdateLobbyDTO;
import com.cheung.tim.server.enums.GameMode;
import com.cheung.tim.server.exception.BadRequestException;
import com.cheung.tim.server.exception.NotFoundException;
import com.cheung.tim.server.repository.LobbyRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.cheung.tim.server.enums.GameStatus.*;
import static com.cheung.tim.server.service.PlayerService.PLAYER_ID_REGEX;
import static org.apache.commons.lang.StringUtils.isBlank;

@Service
public class LobbyService {

    public static final String GAME_NOT_EXIST = "Lobby with id %s does not exist";
    public static final String INVALID_AUTH = "Player id or key invalid";
    public static final String INVALID_PLAYER = "Lobby name or Host not supplied";
    private LobbyRepository lobbyRepository;
    private PlayerService playerService;
    private final ApplicationEventPublisher publisher;

    public LobbyService(LobbyRepository lobbyRepository, PlayerService playerService, ApplicationEventPublisher publisher) {
        this.lobbyRepository = lobbyRepository;
        this.playerService = playerService;
        this.publisher = publisher;
    }

    public Lobby getLobby(Long gameId) {
        Lobby lobby = lobbyRepository.findByLobbyId(gameId);
        if (lobby == null) {
            throw new NotFoundException(String.format(GAME_NOT_EXIST, gameId));
        }
        return lobby;
    }

    public Mono<Lobby> get(Long gameId) {
        Mono<Lobby> lobby = lobbyRepository.findMonoByLobbyId(gameId);
        if (lobby == null) {
            throw new NotFoundException(String.format(GAME_NOT_EXIST, gameId));
        }
        return lobby;
    }

    public Lobby createLobby(CreateLobbyDTO createLobbyDTO) {
        PrivatePlayerDTO hostDTO = createLobbyDTO.getHost();
        if (hostDTO == null || !hostDTO.getId().matches(PLAYER_ID_REGEX) || isBlank(createLobbyDTO.getLobbyName())) {
            throw new BadRequestException(INVALID_PLAYER);
        }
        Player player = getPlayer(hostDTO.getId());
        if (isPlayerInLobby(player)) {
            throw new BadRequestException(String.format("Player %s is already in a game", player.getUsername()));
        }

        GameMode gameMode = GameMode.getEnum(createLobbyDTO.getGameMode());
        if (gameMode != null && gameMode.isEnabled()){
            validatePlayerAuth(hostDTO, player);
            Lobby lobby = new Lobby(createLobbyDTO.getLobbyName(), player, OPEN, createLobbyDTO.getMaxPlayers(),
                    GameMode.valueOf(createLobbyDTO.getGameMode()));
            return lobbyRepository.save(lobby);
        }
        throw new BadRequestException(String.format("Game mode %s is not enabled", createLobbyDTO.getGameMode()));
    }

    @Transactional
    public void updateLobby(Long gameId, UpdateLobbyDTO lobbyDTO) {
        if (lobbyDTO.getHost() == null) {
            throw new BadRequestException(INVALID_PLAYER);
        }
        Player player = getPlayer(lobbyDTO.getHost().getId());
        Lobby lobby = lobbyRepository.findByLobbyId(gameId);
        if (!player.getUserId().equals(lobby.getHost().getUserId())) {
            throw new BadRequestException("Only host can update lobby");
        }
        validatePlayerAuth(lobbyDTO.getHost(), player);
        if (lobby.getGameMode().isValidRounds(lobby, lobbyDTO.getRounds())) {
            lobbyRepository.updateRounds(gameId, lobbyDTO.getRounds());
        } else {
            throw new BadRequestException("Invalid rounds for game mode");
        }
    }

    @Transactional
    public void joinLobby(Long gameId, PrivatePlayerDTO privatePlayerDTO) {
        Player player = getPlayer(privatePlayerDTO.getId());
        if (isPlayerInLobby(player)) {
            throw new BadRequestException(String.format("Player %s is already in a game", player.getUsername()));
        }
        Lobby lobby = lobbyRepository.findByLobbyId(gameId);
        if (lobby == null) {
            throw new NotFoundException(String.format(GAME_NOT_EXIST, gameId));
        } else if (isLobbyFull(lobby)) {
            throw new BadRequestException(String.format("Lobby with id %s is already full", gameId));
        }

        validatePlayerAuth(privatePlayerDTO, player);
        if (isGuestsOneFromFull(lobby)) {
            lobbyRepository.updateStatus(gameId, READY);
        }
        lobbyRepository.updatePlayersCurrentLobby(lobby, player.getUserId());
    }

    @Transactional
    public void leaveLobby(Long gameId, PrivatePlayerDTO privatePlayerDTO) {
        Lobby lobby = lobbyRepository.findByLobbyId(gameId);
        if (lobby == null) {
            throw new NotFoundException(String.format(GAME_NOT_EXIST, gameId));
        }

        if (lobby.getHost() != null && lobby.getHost().equalDTO(privatePlayerDTO)) {
            validatePlayerAuth(privatePlayerDTO, lobby.getHost());
            lobbyRepository.updateStatus(gameId, DELETED);
            lobbyRepository.updateHost(gameId, null);
            lobbyRepository.updatePlayersCurrentLobby(null, privatePlayerDTO.getId());
            lobby.getGuests().forEach(p -> lobbyRepository.updatePlayersCurrentLobby(null, p.getUserId()));
        } else if (lobby.getGuests() != null && lobby.getGuests().contains(new Player(privatePlayerDTO.getId(), privatePlayerDTO.getUsername()))) {
            Player guest = playerService.findPlayerById(privatePlayerDTO.getId());
            validatePlayerAuth(privatePlayerDTO, guest);
            if (isLobbyFull(lobby)) {
                lobbyRepository.updateStatus(gameId, OPEN);
            }
            lobbyRepository.updatePlayersCurrentLobby(null, guest.getUserId());
        } else {
            Player player = playerService.findPlayerById(privatePlayerDTO.getId());
            if (player != null) {
                throw new BadRequestException(String.format("Player %s is not in lobby with id %s", player.getUsername(), gameId));
            }
            throw new NotFoundException(String.format("Player with id %s not found", privatePlayerDTO.getId()));
        }
    }

    public List<Lobby> findOpenLobbies() {
        return lobbyRepository.findByLobbyStatus(OPEN);
    }

    private Player getPlayer(String userId) {
        Player player = playerService.findPlayerById(userId);
        if (player == null) {
            throw new NotFoundException(String.format("Player with id %s not found", userId));
        }
        return player;
    }

    private boolean isPlayerInLobby(Player player) {
        return lobbyRepository.getPlayerInLobby(player.getUserId()) != null;
    }

    private boolean isLobbyFull(Lobby lobby) {
        return lobby.getMaxPlayers() <= getPlayersInLobby(lobby);
    }

    private boolean isGuestsOneFromFull(Lobby lobby) {
        return lobby.getMaxPlayers() - 1 == getPlayersInLobby(lobby);
    }

    private int getPlayersInLobby(Lobby lobby) {
        return 1 + lobby.getGuests().size();
    }

    private void validatePlayerAuth(PrivatePlayerDTO playerDTO, Player player) {
        if (!playerDTO.getKey().equals(player.getKey())) {
            throw new BadRequestException(INVALID_AUTH);
        }
    }
}
