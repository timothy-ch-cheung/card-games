package com.cheung.tim.server.controller;

import com.cheung.tim.server.domain.Game;
import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.GameDTO;
import com.cheung.tim.server.dto.PlayerDTO;
import com.cheung.tim.server.service.GameService;
import com.cheung.tim.server.service.PlayerService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class GameController {

    private ModelMapper modelMapper;

    GameService gameService;
    PlayerService playerService;

    public GameController(GameService gameService, PlayerService playerService, ModelMapper modelMapper) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.modelMapper = modelMapper;
    }

    @GetMapping(path = "/game/{gameId}")
    public String getGame(@PathVariable String gameId) {
        return "Path";
    }

    @PostMapping(path = "/create")
    public ResponseEntity<GameDTO> createGame(@RequestBody GameDTO gameDTO) {
        Player host = playerService.findPlayerById(gameDTO.getHost());
        Game game = gameService.createGame(gameDTO, host);
        return ResponseEntity.ok(convertToDto(game));
    }

    @GetMapping(path = "/games")
    public ResponseEntity<Map<String, Object>> getGames() {
        List<Game> games = gameService.findOpenGames();
        return new ResponseEntity<>(convertToDtoMap(games), HttpStatus.OK);
    }

    private GameDTO convertToDto(Game game) {
        GameDTO gameDto = modelMapper.map(game, GameDTO.class);
        gameDto.setHost(modelMapper.map(game.getPlayer1(), PlayerDTO.class));
        gameDto.setGameStatus(game.getGameStatus().toString());
        return gameDto;
    }

    private Map<String, Object> convertToDtoMap(List<Game> games) {
        Map<String, Object> mapDTO = new HashMap<>();
        List<GameDTO> gamesDto = games.stream().map(this::convertToDto)
                .collect(Collectors.toList());
        mapDTO.put("numOpenGames", gamesDto.size());
        mapDTO.put("games", gamesDto);
        return mapDTO;
    }
}
