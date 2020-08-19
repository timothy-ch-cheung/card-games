package com.cheung.tim.server.service;

import com.cheung.tim.server.domain.Lobby;
import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.PrivatePlayerDTO;
import com.cheung.tim.server.exception.BadRequestException;
import com.cheung.tim.server.exception.NotFoundException;
import com.cheung.tim.server.repository.LobbyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.cheung.tim.server.enums.GameStatus.*;
import static com.cheung.tim.server.service.PlayerService.PLAYER_ID_REGEX;
import static org.apache.commons.lang.StringUtils.isBlank;

@Service
public class LobbyService {

    public static final String GAME_NOT_EXIST = "Lobby with id %s does not exist";
    public static final String INVALID_AUTH = "Player id or key invalid";
    private LobbyRepository lobbyRepository;
    private PlayerService playerService;

    public LobbyService(LobbyRepository lobbyRepository, PlayerService playerService) {
        this.lobbyRepository = lobbyRepository;
        this.playerService = playerService;
    }

    public Lobby getGame(Long gameId) {
        Lobby lobby = lobbyRepository.findByGameId(gameId);
        if (lobby == null) {
            throw new NotFoundException(String.format(GAME_NOT_EXIST, gameId));
        }
        return lobby;
    }

    public Lobby createGame(PrivatePlayerDTO privatePlayerDTO, String lobbyName, Integer maxPlayers) {
        if (privatePlayerDTO == null || !privatePlayerDTO.getId().matches(PLAYER_ID_REGEX) || isBlank(lobbyName)) {
            throw new BadRequestException("Lobby name or Host not supplied");
        }
        Player player = getPlayer(privatePlayerDTO.getId());
        if (isPlayerInGame(player)) {
            throw new BadRequestException(String.format("Player %s is already in a lobby", player.getUsername()));
        } else if (!player.getKey().equals(privatePlayerDTO.getKey())) {
            throw new BadRequestException(INVALID_AUTH);
        }
        Lobby lobby = new Lobby(lobbyName, player, OPEN, maxPlayers);
        return lobbyRepository.save(lobby);
    }

    @Transactional
    public void joinGame(Long gameId, PrivatePlayerDTO privatePlayerDTO) {
        Player player = getPlayer(privatePlayerDTO.getId());
        if (isPlayerInGame(player)) {
            throw new BadRequestException(String.format("Player %s is already in a lobby", player.getUsername()));
        }
        Lobby lobby = lobbyRepository.findByGameId(gameId);
        if (lobby == null) {
            throw new NotFoundException(String.format(GAME_NOT_EXIST, gameId));
        } else if (isGameFull(lobby)) {
            throw new BadRequestException(String.format("Lobby with id %s is already full", gameId));
        } else if (!player.getKey().equals(privatePlayerDTO.getKey())) {
            throw new BadRequestException(INVALID_AUTH);
        }

        if (isGuestsOneFromFull(lobby)) {
            lobbyRepository.updateStatus(gameId, READY);
        }
        lobbyRepository.updatePlayersCurrentGame(lobby, player.getUserId());
    }

    @Transactional
    public void leaveGame(Long gameId, PrivatePlayerDTO privatePlayerDTO) {
        Lobby lobby = lobbyRepository.findByGameId(gameId);
        if (lobby == null) {
            throw new NotFoundException(String.format(GAME_NOT_EXIST, gameId));
        }

        if (lobby.getHost() != null && lobby.getHost().equalDTO(privatePlayerDTO)) {
            if (!lobby.getHost().getKey().equals(privatePlayerDTO.getKey())) {
                throw new BadRequestException(INVALID_AUTH);
            }
            lobbyRepository.updateStatus(gameId, DELETED);
            lobbyRepository.updateHost(gameId, null);
            lobbyRepository.updatePlayersCurrentGame(null, privatePlayerDTO.getId());
            lobby.getGuests().forEach(p -> lobbyRepository.updatePlayersCurrentGame(null, p.getUserId()));
        } else if (lobby.getGuests() != null && lobby.getGuests().contains(new Player(privatePlayerDTO.getId(), privatePlayerDTO.getUsername()))) {
            Player guest = playerService.findPlayerById(privatePlayerDTO.getId());
            if (!guest.getKey().equals(privatePlayerDTO.getKey())) {
                throw new BadRequestException(INVALID_AUTH);
            }
            if (isGameFull(lobby)) {
                lobbyRepository.updateStatus(gameId, OPEN);
            }
            lobbyRepository.updatePlayersCurrentGame(null, guest.getUserId());
        } else {
            Player player = playerService.findPlayerById(privatePlayerDTO.getId());
            if (player != null) {
                throw new BadRequestException(String.format("Player %s is not in lobby with id %s", player.getUsername(), gameId));
            }
            throw new NotFoundException(String.format("Player with id %s not found", privatePlayerDTO.getId()));
        }
    }

    public List<Lobby> findOpenGames() {
        return lobbyRepository.findByGameStatus(OPEN);
    }

    private Player getPlayer(String userId) {
        Player player = playerService.findPlayerById(userId);
        if (player == null) {
            throw new NotFoundException(String.format("Player with id %s not found", userId));
        }
        return player;
    }

    private boolean isPlayerInGame(Player player) {
        return lobbyRepository.getPlayerInGame(player.getUserId()) != null;
    }

    private boolean isGameFull(Lobby lobby) {
        return lobby.getMaxPlayers() <= getPlayersInGame(lobby);
    }

    private boolean isGuestsOneFromFull(Lobby lobby) {
        return lobby.getMaxPlayers() - 1 == getPlayersInGame(lobby);
    }

    private int getPlayersInGame(Lobby lobby) {
        return 1 + lobby.getGuests().size();
    }
}
