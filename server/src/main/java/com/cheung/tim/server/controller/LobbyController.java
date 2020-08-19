package com.cheung.tim.server.controller;

import com.cheung.tim.server.domain.Lobby;
import com.cheung.tim.server.dto.CreateLobbyDTO;
import com.cheung.tim.server.dto.GameDTO;
import com.cheung.tim.server.dto.PrivatePlayerDTO;
import com.cheung.tim.server.service.LobbyService;
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
public class LobbyController {

    private ModelMapper modelMapper;

    LobbyService lobbyService;
    PlayerService playerService;

    public LobbyController(LobbyService lobbyService, PlayerService playerService, ModelMapper modelMapper) {
        this.lobbyService = lobbyService;
        this.playerService = playerService;
        this.modelMapper = modelMapper;
    }

    @GetMapping(path = "/game/{gameId}")
    public ResponseEntity<GameDTO> getGame(@PathVariable Long gameId) {
        Lobby lobby = lobbyService.getGame(gameId);
        return ResponseEntity.ok(convertToDto(lobby));
    }

    @PostMapping(path = "/create")
    public ResponseEntity<GameDTO> createGame(@RequestBody CreateLobbyDTO createLobbyDTO) {
        Lobby lobby = lobbyService.createGame(createLobbyDTO.getHost(), createLobbyDTO.getLobbyName(), createLobbyDTO.getMaxPlayers());
        return ResponseEntity.ok(convertToDto(lobby));
    }

    @PatchMapping(path = "/join/{gameId}")
    public ResponseEntity<Void> joinGame(@PathVariable Long gameId, @RequestBody PrivatePlayerDTO privatePlayerDTO) {
        lobbyService.joinGame(gameId, privatePlayerDTO);
        return ResponseEntity.noContent().header("Content-Length", "0").build();
    }

    @PatchMapping(path = "/leave/{gameId}")
    public ResponseEntity<Void> leaveGame(@PathVariable Long gameId, @RequestBody PrivatePlayerDTO privatePlayerDTO) {
        lobbyService.leaveGame(gameId, privatePlayerDTO);
        return ResponseEntity.noContent().header("Content-Length", "0").build();
    }

    @GetMapping(path = "/games")
    public ResponseEntity<Map<String, Object>> getGames() {
        List<Lobby> lobbies = lobbyService.findOpenGames();
        return new ResponseEntity<>(convertToDtoMap(lobbies), HttpStatus.OK);
    }

    private GameDTO convertToDto(Lobby lobby) {
        GameDTO gameDto = modelMapper.map(lobby, GameDTO.class);
        gameDto.setHost(convertToPublicPlayerDTO(lobby.getHost()));
        gameDto.setGuests(convertToPublicPlayerDTOSet(lobby.getGuests()));
        gameDto.setGameStatus(lobby.getGameStatus().toString());
        return gameDto;
    }

    private Map<String, Object> convertToDtoMap(List<Lobby> lobbies) {
        Map<String, Object> mapDTO = new HashMap<>();
        List<GameDTO> gamesDto = lobbies.stream().map(this::convertToDto)
                .collect(Collectors.toList());
        mapDTO.put("numOpenGames", gamesDto.size());
        mapDTO.put("games", gamesDto);
        return mapDTO;
    }
}
