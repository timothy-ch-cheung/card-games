package com.cheung.tim.server.service;

import com.cheung.tim.server.domain.Game;
import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.PlayerDTO;
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

    private GameRepository gameRepository;
    private PlayerService playerService;

    public GameService(GameRepository gameRepository, PlayerService playerService) {
        this.gameRepository = gameRepository;
        this.playerService = playerService;
    }

    public Game getGame(Long gameId) {
        Game game = gameRepository.findByGameId(gameId);
        if (game == null) {
            throw new NotFoundException(String.format("Game with id %s does not exist", gameId));
        }
        return game;
    }

    public Game createGame(PlayerDTO playerDTO, String lobbyName) throws BadRequestException {
        if (playerDTO == null || !playerDTO.getId().matches(PLAYER_ID_REGEX) || isBlank(lobbyName)) {
            throw new BadRequestException("Lobby name or Host not supplied");
        }
        Player player = getPlayer(playerDTO.getId());
        if (isPlayerInGame(player)) {
            throw new BadRequestException(String.format("Player %s is already in a game", player.getUsername()));
        }
        Game game = new Game(lobbyName, player, OPEN);
        return gameRepository.save(game);
    }

    @Transactional
    public void joinGame(Long gameId, PlayerDTO playerDTO) throws BadRequestException, NotFoundException {
        Player player = getPlayer(playerDTO.getId());
        if (isPlayerInGame(player)) {
            throw new BadRequestException(String.format("Player %s is already in a game", player.getUsername()));
        }
        Game game = gameRepository.findByGameId(gameId);
        if (game == null) {
            throw new NotFoundException(String.format("Game with id %s does not exist", gameId));
        } else if (game.getPlayer2() != null) {
            throw new BadRequestException(String.format("Game with id %s is already full", gameId));
        }
        gameRepository.updatePlayerTwo(gameId, player);
        gameRepository.updateStatus(gameId, READY);
    }

    @Transactional
    public void leaveGame(Long gameId, PlayerDTO playerDTO) throws BadRequestException, NotFoundException {
        Game game = gameRepository.findByGameId(gameId);
        if (game == null) {
            throw new NotFoundException(String.format("Game with id %s does not exist", gameId));
        }

        if (game.getPlayer1() != null && game.getPlayer1().equalId(playerDTO)) {
            gameRepository.updateStatus(gameId, DELETED);
            gameRepository.updatePlayerOne(gameId, null);
        } else if (game.getPlayer2() != null && game.getPlayer2().equalId(playerDTO)) {
            gameRepository.updateStatus(gameId, OPEN);
            gameRepository.updatePlayerTwo(gameId, null);
        } else {
            Player player = getPlayer(playerDTO.getId());
            if (player != null) {
                throw new BadRequestException(String.format("Player %s is not in game with id %s", player.getUsername(), gameId));
            }
            throw new NotFoundException(String.format("Player with id %s not found", playerDTO.getId()));
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
        return gameRepository.countByPlayerOneInGame(player) > 0 || gameRepository.countByPlayerTwoInGame(player) > 0;
    }
}
