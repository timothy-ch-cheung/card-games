package com.cheung.tim.server.service;

import com.cheung.tim.server.domain.Game;
import com.cheung.tim.server.domain.Lobby;
import com.cheung.tim.server.domain.PlayerGameData;
import com.cheung.tim.server.repository.GameRepository;
import com.cheung.tim.server.repository.LobbyRepository;
import org.springframework.stereotype.Service;

@Service
public class GameOrchestratorService {

    private LobbyRepository lobbyRepository;
    private GameRepository gameRepository;

    public GameOrchestratorService(LobbyRepository lobbyRepository, GameRepository gameRepository) {
        this.lobbyRepository = lobbyRepository;
        this.gameRepository = gameRepository;
    }

    public Game handleTurn(Long lobbyId, GameHandler handler) {
        Lobby lobby = lobbyRepository.findByLobbyId(lobbyId);
        Game game = lobby.getGame();

        PlayerGameData currentPlayer = handler.determineNextPlayer(game);

        if (handler.isGameEnded(game)) {
            game.setWinner(handler.getWinningPlayer(game));
        } else {
            handler.executeTurn(currentPlayer, game);
        }

        gameRepository.save(game);
        return game;
    }
}
