package com.cheung.tim.server.controller;

import com.cheung.tim.server.domain.Lobby;
import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.LobbyDTO;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static com.cheung.tim.server.enums.GameMode.MATCH_TWO;
import static com.cheung.tim.server.enums.GameStatus.DELETED;
import static com.cheung.tim.server.enums.GameStatus.READY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = LobbySocketController.class)
class LobbySocketControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SimpMessagingTemplate template;

    @MockBean
    ModelMapper modelMapper;

    LobbySocketController socket;
    LobbyDTO lobbyDTO;

    @BeforeEach
    public void setup() {
        this.socket = new LobbySocketController(modelMapper, template);
        lobbyDTO = new LobbyDTO();
        lobbyDTO.setLobbyName("test_lobby");
        when(modelMapper.map(any(), any())).thenReturn(lobbyDTO);
    }

    @Test
    void broadcastLobby_shouldSendEmptyJSONWhenGameDeleted() throws IllegalAccessException {
        when(modelMapper.map(any(), any())).thenReturn(new LobbyDTO());
        Lobby lobby = new Lobby("test_lobby", new Player(), DELETED, 2, MATCH_TWO);
        FieldUtils.writeField(lobby, "lobbyId", 1L, true);

        socket.broadcastLobby(lobby);
        verify(template).convertAndSend(eq("/topic/game/1"), eq("{}"));
    }

    @Test
    void broadcastLobby_shouldSendLobby() throws IllegalAccessException {
        Lobby lobby = new Lobby("test_lobby", new Player(), READY, 2, MATCH_TWO);
        FieldUtils.writeField(lobby, "lobbyId", 1L, true);

        socket.broadcastLobby(lobby);
        verify(template).convertAndSend(eq("/topic/game/1"), eq(lobbyDTO));
    }
}