package com.cheung.tim.server.controller;

import com.cheung.tim.server.domain.Game;
import com.cheung.tim.server.dto.CreateLobbyDTO;
import com.cheung.tim.server.dto.GameDTO;
import com.cheung.tim.server.dto.PrivatePlayerDTO;
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

import static com.cheung.tim.server.dto.PrivatePlayerDTO.convertToPublicPlayerDTO;
import static com.cheung.tim.server.dto.PublicPlayerDTO.convertToPublicPlayerDTOSet;

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
    public ResponseEntity<GameDTO> getGame(@PathVariable Long gameId) {
        Game game = gameService.getGame(gameId);
        return ResponseEntity.ok(convertToDto(game));
    }

    @PostMapping(path = "/create")
    public ResponseEntity<GameDTO> createGame(@RequestBody CreateLobbyDTO createLobbyDTO) {
        Game game = gameService.createGame(createLobbyDTO.getHost(), createLobbyDTO.getLobbyName(), createLobbyDTO.getMaxPlayers());
        return ResponseEntity.ok(convertToDto(game));
    }

    @PatchMapping(path = "/join/{gameId}")
    public ResponseEntity<Void> joinGame(@PathVariable Long gameId, @RequestBody PrivatePlayerDTO privatePlayerDTO) {
        gameService.joinGame(gameId, privatePlayerDTO);
        return ResponseEntity.noContent().header("Content-Length", "0").build();
    }

    @PatchMapping(path = "/leave/{gameId}")
    public ResponseEntity<Void> leaveGame(@PathVariable Long gameId, @RequestBody PrivatePlayerDTO privatePlayerDTO) {
        gameService.leaveGame(gameId, privatePlayerDTO);
        return ResponseEntity.noContent().header("Content-Length", "0").build();
    }

    @GetMapping(path = "/games")
    public ResponseEntity<Map<String, Object>> getGames() {
        List<Game> games = gameService.findOpenGames();
        return new ResponseEntity<>(convertToDtoMap(games), HttpStatus.OK);
    }

    private GameDTO convertToDto(Game game) {
        GameDTO gameDto = modelMapper.map(game, GameDTO.class);
        gameDto.setHost(convertToPublicPlayerDTO(game.getHost()));
        gameDto.setGuests(convertToPublicPlayerDTOSet(game.getGuests()));
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
