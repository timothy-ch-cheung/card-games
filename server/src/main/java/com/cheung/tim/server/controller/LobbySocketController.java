package com.cheung.tim.server.controller;

import com.cheung.tim.server.domain.Lobby;
import com.cheung.tim.server.service.LobbyService;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import static com.cheung.tim.server.controller.LobbyController.convertToDto;

@Controller
public class LobbySocketController {

    public static final String GAME_TOPIC = "/topic/game/";
    private LobbyService lobbyService;
    private ModelMapper modelMapper;
    private SimpMessagingTemplate template;

    public LobbySocketController(LobbyService lobbyService, ModelMapper modelMapper, SimpMessagingTemplate template) {
        this.lobbyService = lobbyService;
        this.modelMapper = modelMapper;
        this.template = template;
    }

    public void broadcastLobby(@DestinationVariable Long gameId) {
        Lobby lobby = lobbyService.getLobby(gameId);
        if (lobby != null) {
            this.template.convertAndSend(GAME_TOPIC + gameId, convertToDto(lobbyService.getLobby(gameId), modelMapper));
        } else {
            this.template.convertAndSend(GAME_TOPIC + gameId, "{}");
        }
    }
}
