package com.cheung.tim.server.controller;

import com.cheung.tim.server.domain.Lobby;
import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.GameDTO;
import com.cheung.tim.server.dto.PrivatePlayerDTO;
import com.cheung.tim.server.enums.GameStatus;
import com.cheung.tim.server.exception.BadRequestException;
import com.cheung.tim.server.exception.NotFoundException;
import com.cheung.tim.server.service.LobbyService;
import com.cheung.tim.server.service.PlayerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = LobbyController.class)
class LobbyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    LobbyService lobbyService;

    @MockBean
    PlayerService playerService;

    @MockBean
    ModelMapper modelMapper;

    ArgumentCaptor playerDTOCapture = ArgumentCaptor.forClass(PrivatePlayerDTO.class);
    ArgumentCaptor lobbyNameCapture = ArgumentCaptor.forClass(String.class);

    @Test
    void getGame_shouldReturn200() throws Exception {
        Player player = new Player("opjps1w7o66ckmthc18zo32r29wic9fo","John Smith");
        Lobby lobby = new Lobby("test_lobby", player, GameStatus.OPEN, 2);
        GameDTO gameDTO = getGameDTO();

        when(lobbyService.getGame(anyLong())).thenReturn(lobby);
        when(modelMapper.map(lobby, GameDTO.class)).thenReturn(gameDTO);

        String expectedJson = "{\n" +
                "   \"id\":null,\n" +
                "   \"createdAt\":null,\n" +
                "   \"lobbyName\":\"test_lobby\",\n" +
                "   \"host\":{\n" +
                "      \"username\":\"John Smith\",\n" +
                "      \"id\":\"opjps1w7o66ckmthc18zo32r29wic9fo\"\n" +
                "   },\n" +
                "   \"guests\": [],\n" +
                "   \"gameStatus\":\"OPEN\",\n" +
                "   \"maxPlayers\": 2\n" +
                "}";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/lobby/1")
        ).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertThat(response.getStatus(), is(200));
        assertThat(response.getContentAsString(), sameJSONAs(expectedJson));
    }

    @Test
    void getGame_shouldThrowNotFoundException() throws Exception {
        when(lobbyService.getGame(anyLong())).thenThrow(new NotFoundException("not found"));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/game/1")).andReturn();
        MockHttpServletResponse response = result.getResponse();

        assertThat(response.getStatus(), is(404));
    }

    @Test
    void createGame_shouldReturn200() throws Exception {
        Player player = new Player("opjps1w7o66ckmthc18zo32r29wic9fo", "John Smith");
        Lobby lobby = new Lobby("test_lobby", player, GameStatus.OPEN, 2);
        GameDTO gameDTO = getGameDTO();

        when(lobbyService.createGame(any(PrivatePlayerDTO.class), anyString(), anyInt())).thenReturn(lobby);
        when(modelMapper.map(lobby, GameDTO.class)).thenReturn(gameDTO);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getCreateRequest())
        ).andReturn();

        MockHttpServletResponse response = result.getResponse();

        String expectedJson = "{\n" +
                "   \"id\":null,\n" +
                "   \"createdAt\":null,\n" +
                "   \"lobbyName\":\"test_lobby\",\n" +
                "   \"host\":{\n" +
                "      \"username\":\"John Smith\",\n" +
                "      \"id\":\"opjps1w7o66ckmthc18zo32r29wic9fo\"\n" +
                "   },\n" +
                "   \"guests\": [],\n" +
                "   \"gameStatus\":\"OPEN\",\n" +
                "   \"maxPlayers\": 2\n" +
                "}";

        verify(lobbyService).createGame((PrivatePlayerDTO) playerDTOCapture.capture(), (String) lobbyNameCapture.capture(), any(Integer.class));
        assertThat(lobbyNameCapture.getAllValues().size(), is(1));

        assertThat(response.getStatus(), is(200));
        assertThat(response.getContentAsString(), sameJSONAs(expectedJson));
    }

    @Test
    void createGame_shouldThrowBadRequestException() throws Exception {
        when(lobbyService.createGame(any(PrivatePlayerDTO.class), anyString(), anyInt())).thenThrow(new BadRequestException(""));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getCreateRequest())).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertThat(response.getStatus(), is(400));
    }

    @Test
    void getGames_shouldReturn200() throws Exception {
        List<Lobby> lobbies = new ArrayList();
        Player player = new Player("opjps1w7o66ckmthc18zo32r29wic9fo","John Smith");
        Lobby lobby = new Lobby("test_lobby", player, GameStatus.OPEN, 2);
        lobbies.add(lobby);
        when(lobbyService.findOpenGames()).thenReturn(lobbies);

        when(modelMapper.map(lobby, GameDTO.class)).thenReturn(getGameDTO());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/lobbies").content("")).andReturn();

        MockHttpServletResponse response = result.getResponse();

        String expectedJson = "{\n" +
                "   \"numOpenGames\": 1,\n" +
                "   \"lobbies\": [\n" +
                "      {\n" +
                "         \"id\": null,\n" +
                "         \"createdAt\": null,\n" +
                "         \"lobbyName\": \"test_lobby\",\n" +
                "         \"host\": {\n" +
                "            \"username\": \"John Smith\",\n" +
                "            \"id\":\"opjps1w7o66ckmthc18zo32r29wic9fo\"\n" +
                "         },\n" +
                "         \"guests\": [],\n" +
                "         \"gameStatus\": \"OPEN\",\n" +
                "         \"maxPlayers\": 2\n" +
                "      }\n" +
                "   ]\n" +
                "}";

        assertThat(response.getStatus(), is(200));
        assertThat(response.getContentAsString(), sameJSONAs(expectedJson));
    }

    @Test
    void joinGame_shouldReturn204() throws Exception {
        MvcResult result = performPatch("/join/1");

        MockHttpServletResponse response = result.getResponse();

        assertThat(response.getStatus(), is(204));
        assertThat(response.getContentAsString(), is(""));
    }

    @Test
    void joinGame_shouldThrowBadRequestException() throws Exception {
        doThrow(new BadRequestException("")).when(lobbyService).joinGame(any(Long.class), any(PrivatePlayerDTO.class));

        MvcResult result = performPatch("/join/1");
        MockHttpServletResponse response = result.getResponse();

        assertThat(response.getStatus(), is(400));
    }

    @Test
    void joinGame_shouldThrowNotFoundException() throws Exception {
        doThrow(new NotFoundException("not found")).when(lobbyService).joinGame(any(Long.class), any(PrivatePlayerDTO.class));

        MvcResult result = performPatch("/join/1");
        MockHttpServletResponse response = result.getResponse();

        assertThat(response.getStatus(), is(404));
    }

    @Test
    void leaveGame_shouldReturn204() throws Exception {
        MvcResult result = performPatch("/leave/1");

        MockHttpServletResponse response = result.getResponse();

        assertThat(response.getStatus(), is(204));
        assertThat(response.getContentAsString(), is(""));
    }

    @Test
    void leaveGame_shouldThrowBadRequestException() throws Exception {
        doThrow(new BadRequestException("bad request")).when(lobbyService).leaveGame(any(Long.class), any(PrivatePlayerDTO.class));

        MvcResult result = performPatch("/leave/1");
        MockHttpServletResponse response = result.getResponse();

        assertThat(response.getStatus(), is(400));
    }

    @Test
    void leaveGame_shouldThrowNotFoundException() throws Exception {
        doThrow(new NotFoundException("not found")).when(lobbyService).leaveGame(any(Long.class), any(PrivatePlayerDTO.class));

        MvcResult result = performPatch("/leave/1");
        MockHttpServletResponse response = result.getResponse();

        assertThat(response.getStatus(), is(404));
    }

    private MvcResult performPatch(String endpoint) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .patch(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"40283481721d879601721d87b6350000\"}")
        ).andReturn();
    }

    private GameDTO getGameDTO() {
        GameDTO gameDTO = new GameDTO();
        gameDTO.setLobbyName("test_lobby");
        gameDTO.setGameStatus("OPEN");
        gameDTO.setMaxPlayers(2);
        return gameDTO;
    }

    private String getCreateRequest() {
        return "{\n" +
                "  \"lobbyName\": \"test_lobby\",\n" +
                "  \"host\": {\n" +
                "    \"id\": \"40283481721d879601721d87b6350000\"\n" +
                "  },\n" +
                "  \"maxPlayers\": 2\n" +
                "}";
    }
}