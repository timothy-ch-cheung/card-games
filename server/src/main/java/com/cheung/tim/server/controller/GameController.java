package com.cheung.tim.server.controller;

import com.cheung.tim.server.domain.Game;
import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.GameDTO;
import com.cheung.tim.server.enums.GameStatus;
import com.cheung.tim.server.repository.GameRepository;
import com.cheung.tim.server.repository.PlayerRepository;
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

    GameRepository gameRepository;
    PlayerRepository playerRepository;

    public GameController(GameRepository gameRepository, PlayerRepository playerRepository, ModelMapper modelMapper) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.modelMapper = modelMapper;
    }

    @GetMapping(path = "/game/{gameId}")
    public String getGame(@PathVariable String gameId) {
        return "Path";
    }

    @PostMapping(path = "/game")
    public ResponseEntity<GameDTO> createGame(@RequestBody Game newGame) {
        Player host = playerRepository.findById(newGame.getPlayer1().getUserId()).get();
        if (host == null) {
            return ResponseEntity.badRequest().build();
        }
        newGame.setPlayer1(host);
        newGame.setGameStatus(GameStatus.OPEN);
        return new ResponseEntity<>(convertToDto(this.gameRepository.save(newGame)), HttpStatus.OK);
    }

    @GetMapping(path = "/games")
    public ResponseEntity<Map<String, Object>> getGames() {
        List<Game> games = gameRepository.findByGameStatus(GameStatus.OPEN);
        return new ResponseEntity<>(convertToDtoMap(games), HttpStatus.OK);
    }

    private GameDTO convertToDto(Game game) {
        GameDTO gameDto = modelMapper.map(game, GameDTO.class);
        gameDto.setHost(game.getPlayer1().getUsername());
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
