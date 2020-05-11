package com.cheung.tim.server.controller;

import com.cheung.tim.server.domain.Game;
import com.cheung.tim.server.enums.GameStatus;
import com.cheung.tim.server.repository.GameRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GameController {

    GameRepository gameRepository;

    public GameController(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @GetMapping(path = "/game/{gameId}")
    public String getGame(@PathVariable String gameId) {
        return "Path";
    }

    @GetMapping(path = "/games")
    public ResponseEntity<List<Game>> getGames() {
        return new ResponseEntity<>(gameRepository.findByGameStatus(GameStatus.OPEN), HttpStatus.OK);
    }

}
