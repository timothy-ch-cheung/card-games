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

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Game createGame(GameDTO gameDTO, Player player) throws BadRequestException {
        if (player == null){
            throw new BadRequestException("Player does not exist");
        }
        Game game = new Game(gameDTO.getLobbyName(), player, GameStatus.OPEN);
        return gameRepository.save(game);
    }

    public List<Game> findOpenGames() {
        return gameRepository.findByGameStatus(GameStatus.OPEN);
    }
}
