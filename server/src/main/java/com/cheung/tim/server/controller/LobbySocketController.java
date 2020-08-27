package com.cheung.tim.server.controller;

import com.cheung.tim.server.dto.LobbyDTO;
import com.cheung.tim.server.service.LobbyService;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import static com.cheung.tim.server.controller.LobbyController.convertToDto;

@Controller
public class LobbySocketController {

    private LobbyService lobbyService;
    private ModelMapper modelMapper;

    public LobbySocketController(LobbyService lobbyService, ModelMapper modelMapper) {
        this.lobbyService = lobbyService;
        this.modelMapper = modelMapper;
    }

    @SubscribeMapping("/game/{gameId}")
    @SendTo("/topic/game/{gameId}")
    public LobbyDTO getLobby(@DestinationVariable Long gameId) {
        return convertToDto(lobbyService.getLobby(gameId), modelMapper);
    }
}
