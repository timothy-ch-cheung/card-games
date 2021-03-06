package com.cheung.tim.server.controller;

import com.cheung.tim.server.domain.Lobby;
import com.cheung.tim.server.dto.CreateLobbyDTO;
import com.cheung.tim.server.dto.LobbyDTO;
import com.cheung.tim.server.dto.PrivatePlayerDTO;
import com.cheung.tim.server.dto.UpdateLobbyDTO;
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

    public static final String CONTENT_LENGTH = "Content-Length";
    private ModelMapper modelMapper;

    LobbyService lobbyService;
    LobbySocketController lobbySocketController;
    PlayerService playerService;

    public LobbyController(LobbyService lobbyService, LobbySocketController lobbySocketController, PlayerService playerService, ModelMapper modelMapper) {
        this.lobbyService = lobbyService;
        this.lobbySocketController = lobbySocketController;
        this.playerService = playerService;
        this.modelMapper = modelMapper;
    }

    @GetMapping(path = "/game/{gameId}")
    public ResponseEntity<LobbyDTO> getLobby(@PathVariable Long gameId) {
        Lobby lobby = lobbyService.getLobby(gameId);
        return ResponseEntity.ok(convertToDto(lobby));
    }

    @PostMapping(path = "/create")
    public ResponseEntity<LobbyDTO> createLobby(@RequestBody CreateLobbyDTO createLobbyDTO) {
        Lobby lobby = lobbyService.createLobby(createLobbyDTO);
        return ResponseEntity.ok(convertToDto(lobby));
    }

    @PatchMapping(path = "/update/{gameId}")
    public ResponseEntity<Void> updateLobby(@PathVariable Long gameId, @RequestBody UpdateLobbyDTO updateLobbyDTO) {
        Lobby lobby = lobbyService.updateLobby(gameId, updateLobbyDTO);
        lobbySocketController.broadcastLobby(lobby);
        return ResponseEntity.noContent().header(CONTENT_LENGTH, "0").build();
    }

    @PatchMapping(path = "/join/{gameId}")
    public ResponseEntity<Void> joinLobby(@PathVariable Long gameId, @RequestBody PrivatePlayerDTO privatePlayerDTO) {
        Lobby lobby = lobbyService.joinLobby(gameId, privatePlayerDTO);
        lobbySocketController.broadcastLobby(lobby);
        return ResponseEntity.noContent().header(CONTENT_LENGTH, "0").build();
    }

    @PatchMapping(path = "/leave/{gameId}")
    public ResponseEntity<Void> leaveLobby(@PathVariable Long gameId, @RequestBody PrivatePlayerDTO privatePlayerDTO) {
        Lobby lobby = lobbyService.leaveLobby(gameId, privatePlayerDTO);
        lobbySocketController.broadcastLobby(lobby);
        return ResponseEntity.noContent().header(CONTENT_LENGTH, "0").build();
    }

    @GetMapping(path = "/games")
    public ResponseEntity<Map<String, Object>> getLobbies() {
        List<Lobby> lobbies = lobbyService.findOpenLobbies();
        return new ResponseEntity<>(convertToDtoMap(lobbies), HttpStatus.OK);
    }

    static LobbyDTO convertToDto(Lobby lobby, ModelMapper modelMapper) {
        LobbyDTO lobbyDto = modelMapper.map(lobby, LobbyDTO.class);
        lobbyDto.setHost(convertToPublicPlayerDTO(lobby.getHost()));
        lobbyDto.setGuests(convertToPublicPlayerDTOSet(lobby.getGuests()));
        lobbyDto.setGameStatus(lobby.getGameStatus().toString());
        lobbyDto.setGameMode(lobby.getGameMode().toString());
        return lobbyDto;
    }

    private LobbyDTO convertToDto(Lobby lobby) {
        return convertToDto(lobby, modelMapper);
    }

    private Map<String, Object> convertToDtoMap(List<Lobby> lobbies) {
        Map<String, Object> mapDTO = new HashMap<>();
        List<LobbyDTO> gamesDto = lobbies.stream().map(this::convertToDto)
                .collect(Collectors.toList());
        mapDTO.put("numOpenGames", gamesDto.size());
        mapDTO.put("games", gamesDto);
        return mapDTO;
    }
}
