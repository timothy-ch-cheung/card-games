package com.cheung.tim.server.controller;

import com.cheung.tim.server.domain.Lobby;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import static com.cheung.tim.server.controller.LobbyController.convertToDto;
import static com.cheung.tim.server.enums.GameStatus.DELETED;

@Controller
public class LobbySocketController {

    public static final String GAME_TOPIC = "/topic/game/";
    private ModelMapper modelMapper;
    private SimpMessagingTemplate template;

    public LobbySocketController(ModelMapper modelMapper, SimpMessagingTemplate template) {
        this.modelMapper = modelMapper;
        this.template = template;
    }

    public void broadcastLobby(Lobby lobby) {
        Long gameId = lobby.getLobbyId();
        if (DELETED.equals(lobby.getGameStatus())) {
            this.template.convertAndSend(GAME_TOPIC + gameId, "{}");
        } else {
            this.template.convertAndSend(GAME_TOPIC + gameId, convertToDto(lobby, modelMapper));
        }
    }
}
