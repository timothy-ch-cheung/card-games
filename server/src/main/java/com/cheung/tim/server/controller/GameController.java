package com.cheung.tim.server.controller;

import com.cheung.tim.server.domain.Game;
import com.cheung.tim.server.dto.GameDTO;
import com.cheung.tim.server.enums.GameStatus;
import com.cheung.tim.server.repository.GameRepository;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class GameController {

    private ModelMapper modelMapper;

    GameRepository gameRepository;

    public GameController(GameRepository gameRepository, ModelMapper modelMapper) {
        this.gameRepository = gameRepository;
        this.modelMapper = modelMapper;
    }

    @GetMapping(path = "/game/{gameId}")
    public String getGame(@PathVariable String gameId) {
        return "Path";
    }

    @GetMapping(path = "/games")
    @ResponseBody
    public List<GameDTO> getGames() {
        List<Game> games = gameRepository.findByGameStatus(GameStatus.OPEN);
        return games.stream().map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private GameDTO convertToDto(Game post) {
        GameDTO gameDto = modelMapper.map(post, GameDTO.class);
        return gameDto;
    }
}
