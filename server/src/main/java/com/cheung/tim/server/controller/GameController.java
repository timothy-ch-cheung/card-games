package com.cheung.tim.server.controller;

import com.cheung.tim.server.domain.Game;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    @GetMapping(path = "/game/{gameId}")
    public String getGame(@PathVariable String gameId) {
        return "Path";
    }

    @GetMapping(path = "/games")
    public String getGames() {
        return "Games";
    }

}
