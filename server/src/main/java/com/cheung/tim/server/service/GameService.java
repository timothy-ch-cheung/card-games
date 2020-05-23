package com.cheung.tim.server.service;

import com.cheung.tim.server.domain.Game;
import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.GameDTO;
import com.cheung.tim.server.dto.PlayerDTO;
import com.cheung.tim.server.enums.GameStatus;
import com.cheung.tim.server.exception.BadRequestException;
import com.cheung.tim.server.repository.GameRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.cheung.tim.server.enums.GameStatus.*;

@Service
public class GameService {

    private GameRepository gameRepository;
    private PlayerService playerService;

    public GameService(GameRepository gameRepository, PlayerService playerService) {
        this.gameRepository = gameRepository;
        this.playerService = playerService;
    }

    public Game createGame(GameDTO gameDTO) throws BadRequestException {
        Player player = getPlayer(gameDTO.getHost().getId());
        if (isPlayerInGame(player)) {
            throw new BadRequestException(String.format("Player %s is already in a game", gameDTO.getHost().getUsername()));
        }
        Game game = new Game(gameDTO.getLobbyName(), player, OPEN);
        return gameRepository.save(game);
    }

    @Transactional
    public void joinGame(Long gameId, PlayerDTO playerDTO) {
        Player player = getPlayer(playerDTO.getId());
        if (isPlayerInGame(player)) {
            throw new BadRequestException(String.format("Player %s is already in a game", player.getUsername()));
        }
        Game game = gameRepository.findByGameId(gameId);
        if (game.getPlayer2() != null) {
            throw new BadRequestException(String.format("Game with id %s is already full", gameId));
        }
        gameRepository.updatePlayerTwo(gameId, player);
        gameRepository.updateStatus(gameId, READY);
    }

    @Transactional
    public void leaveGame(Long gameId, PlayerDTO playerDTO) {
        Game game = gameRepository.findByGameId(gameId);
        if (game == null) {
            throw new BadRequestException(String.format("Game with id {} does not exist", gameId));
        }

        if (game.getPlayer1() != null && game.getPlayer1().equalId(playerDTO)) {
            gameRepository.updateStatus(gameId, DELETED);
        } else if (game.getPlayer2() != null && game.getPlayer2().equalId(playerDTO)) {
            gameRepository.updateStatus(gameId, OPEN);
            gameRepository.updatePlayerTwo(gameId, null);
        } else {
            throw new BadRequestException(String.format("Player {} is not in game with id {}", playerDTO.getUsername(), gameId));
        }
    }

    public List<Game> findOpenGames() {
        return gameRepository.findByGameStatus(OPEN);
    }

    private Player getPlayer(String userId) {
        Player player = playerService.findPlayerById(userId);
        if (player == null) {
            throw new BadRequestException("Player does not exist");
        }
        return player;
    }

    private boolean isPlayerInGame(Player player) {
        return gameRepository.countByPlayerOneInGame(player) > 0 || gameRepository.countByPlayerTwoInGame(player) > 0;
    }
}
