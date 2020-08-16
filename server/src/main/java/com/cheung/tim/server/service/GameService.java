package com.cheung.tim.server.service;

import com.cheung.tim.server.domain.Game;
import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.PrivatePlayerDTO;
import com.cheung.tim.server.exception.BadRequestException;
import com.cheung.tim.server.exception.NotFoundException;
import com.cheung.tim.server.repository.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.cheung.tim.server.enums.GameStatus.*;
import static com.cheung.tim.server.service.PlayerService.PLAYER_ID_REGEX;
import static org.apache.commons.lang.StringUtils.isBlank;

@Service
public class GameService {

    public static final String GAME_NOT_EXIST = "Game with id %s does not exist";
    public static final String INVALID_AUTH = "Player id or key invalid";
    private GameRepository gameRepository;
    private PlayerService playerService;

    public GameService(GameRepository gameRepository, PlayerService playerService) {
        this.gameRepository = gameRepository;
        this.playerService = playerService;
    }

    public Game getGame(Long gameId) {
        Game game = gameRepository.findByGameId(gameId);
        if (game == null) {
            throw new NotFoundException(String.format(GAME_NOT_EXIST, gameId));
        }
        return game;
    }

    public Game createGame(PrivatePlayerDTO privatePlayerDTO, String lobbyName, Integer maxPlayers) {
        if (privatePlayerDTO == null || !privatePlayerDTO.getId().matches(PLAYER_ID_REGEX) || isBlank(lobbyName)) {
            throw new BadRequestException("Lobby name or Host not supplied");
        }
        Player player = getPlayer(privatePlayerDTO.getId());
        if (isPlayerInGame(player)) {
            throw new BadRequestException(String.format("Player %s is already in a game", player.getUsername()));
        } else if (!player.getKey().equals(privatePlayerDTO.getKey())) {
            throw new BadRequestException(INVALID_AUTH);
        }
        Game game = new Game(lobbyName, player, OPEN, maxPlayers);
        return gameRepository.save(game);
    }

    @Transactional
    public void joinGame(Long gameId, PrivatePlayerDTO privatePlayerDTO) {
        Player player = getPlayer(privatePlayerDTO.getId());
        if (isPlayerInGame(player)) {
            throw new BadRequestException(String.format("Player %s is already in a game", player.getUsername()));
        }
        Game game = gameRepository.findByGameId(gameId);
        if (game == null) {
            throw new NotFoundException(String.format(GAME_NOT_EXIST, gameId));
        } else if (isGameFull(game)) {
            throw new BadRequestException(String.format("Game with id %s is already full", gameId));
        } else if (!player.getKey().equals(privatePlayerDTO.getKey())) {
            throw new BadRequestException(INVALID_AUTH);
        }

        if (isGuestsOneFromFull(game)) {
            gameRepository.updateStatus(gameId, READY);
        }
        gameRepository.updatePlayersCurrentGame(game, player.getUserId());
    }

    @Transactional
    public void leaveGame(Long gameId, PrivatePlayerDTO privatePlayerDTO) {
        Game game = gameRepository.findByGameId(gameId);
        if (game == null) {
            throw new NotFoundException(String.format(GAME_NOT_EXIST, gameId));
        }

        if (game.getHost() != null && game.getHost().equalDTO(privatePlayerDTO)) {
            if (!game.getHost().getKey().equals(privatePlayerDTO.getKey())) {
                throw new BadRequestException(INVALID_AUTH);
            }
            gameRepository.updateStatus(gameId, DELETED);
            gameRepository.updateHost(gameId, null);
            gameRepository.updatePlayersCurrentGame(null, privatePlayerDTO.getId());
            game.getGuests().forEach(p -> gameRepository.updatePlayersCurrentGame(null, p.getUserId()));
        } else if (game.getGuests() != null && game.getGuests().contains(new Player(privatePlayerDTO.getId(), privatePlayerDTO.getUsername()))) {
            Player guest = playerService.findPlayerById(privatePlayerDTO.getId());
            if (!guest.getKey().equals(privatePlayerDTO.getKey())) {
                throw new BadRequestException(INVALID_AUTH);
            }
            if (isGameFull(game)) {
                gameRepository.updateStatus(gameId, OPEN);
            }
            gameRepository.updatePlayersCurrentGame(null, guest.getUserId());
        } else {
            Player player = playerService.findPlayerById(privatePlayerDTO.getId());
            if (player != null) {
                throw new BadRequestException(String.format("Player %s is not in game with id %s", player.getUsername(), gameId));
            }
            throw new NotFoundException(String.format("Player with id %s not found", privatePlayerDTO.getId()));
        }
    }

    public List<Game> findOpenGames() {
        return gameRepository.findByGameStatus(OPEN);
    }

    private Player getPlayer(String userId) {
        Player player = playerService.findPlayerById(userId);
        if (player == null) {
            throw new NotFoundException(String.format("Player with id %s not found", userId));
        }
        return player;
    }

    private boolean isPlayerInGame(Player player) {
        return gameRepository.getPlayerInGame(player.getUserId()) != null;
    }

    private boolean isGameFull(Game game) {
        return game.getMaxPlayers() <= getPlayersInGame(game);
    }

    private boolean isGuestsOneFromFull(Game game) {
        return game.getMaxPlayers() - 1 == getPlayersInGame(game);
    }

    private int getPlayersInGame(Game game) {
        return 1 + game.getGuests().size();
    }
}
