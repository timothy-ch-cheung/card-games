package com.cheung.tim.server.service;

import com.cheung.tim.server.domain.Game;
import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.GameDTO;
import com.cheung.tim.server.enums.GameStatus;
import com.cheung.tim.server.exception.BadRequestException;
import com.cheung.tim.server.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    private GameRepository gameRepository;
    private PlayerService playerService;

    public GameService(GameRepository gameRepository, PlayerService playerService) {
        this.gameRepository = gameRepository;
        this.playerService = playerService;
    }

    public Game createGame(GameDTO gameDTO) throws BadRequestException {
        Player player = playerService.findPlayerById(gameDTO.getHost());
        if (player == null) {
            throw new BadRequestException("Player does not exist");
        } else if (gameRepository.countByPlayer(player) > 0) {
            throw new BadRequestException(String.format("Player %s is already in a game", gameDTO.getHost().getUsername()));
        }
        Game game = new Game(gameDTO.getLobbyName(), player, GameStatus.OPEN);
        return gameRepository.save(game);
    }

    public List<Game> findOpenGames() {
        return gameRepository.findByGameStatus(GameStatus.OPEN);
    }
}
