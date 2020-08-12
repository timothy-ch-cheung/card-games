package com.cheung.tim.server.controller;

import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.PrivatePlayerDTO;
import com.cheung.tim.server.dto.PublicPlayerDTO;
import com.cheung.tim.server.service.PlayerService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.cheung.tim.server.dto.PrivatePlayerDTO.convertToPrivatePlayerDTO;

@RestController
public class PlayerController {

    private ModelMapper modelMapper;

    PlayerService playerService;

    public PlayerController(PlayerService playerService, ModelMapper modelMapper) {
        this.playerService = playerService;
        this.modelMapper = modelMapper;
    }

    @PostMapping(path = "/player")
    public ResponseEntity<PrivatePlayerDTO> createPlayer(@RequestBody PrivatePlayerDTO player) {
        return ResponseEntity.ok(convertToPrivateDTO(this.playerService.createPlayer(player)));
    }

    @GetMapping(path = "/player/{userId}")
    public ResponseEntity<PublicPlayerDTO> getPlayer(@PathVariable String userId) {
        return ResponseEntity.ok(convertToPublicDTO(this.playerService.findPlayerById(userId)));
    }

    private PublicPlayerDTO convertToPublicDTO(Player player) {
        return modelMapper.map(player, PublicPlayerDTO.class);
    }

    private PrivatePlayerDTO convertToPrivateDTO(Player player) {
        return modelMapper.map(player, PrivatePlayerDTO.class);
    }
}
