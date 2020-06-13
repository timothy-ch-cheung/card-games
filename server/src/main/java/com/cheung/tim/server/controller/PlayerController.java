package com.cheung.tim.server.controller;

import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.PlayerDTO;
import com.cheung.tim.server.service.PlayerService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PlayerController {

    private ModelMapper modelMapper;

    PlayerService playerService;

    public PlayerController(PlayerService playerService, ModelMapper modelMapper) {
        this.playerService = playerService;
        this.modelMapper = modelMapper;
    }

    @PostMapping(path = "/player")
    public ResponseEntity<PlayerDTO> createPlayer(@RequestBody PlayerDTO player) {
        return ResponseEntity.ok(convertToDTO(this.playerService.createPlayer(player)));
    }

    @GetMapping(path = "/player/{userId}")
    public ResponseEntity<PlayerDTO> getPlayer(@PathVariable String userId) {
        return ResponseEntity.ok(convertToDTO(this.playerService.findPlayerById(userId)));
    }

    private PlayerDTO convertToDTO(Player player) {
        return modelMapper.map(player, PlayerDTO.class);
    }
}
