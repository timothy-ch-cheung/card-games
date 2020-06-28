package com.cheung.tim.server.controller;

import com.cheung.tim.server.domain.Game;
import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.GameDTO;
import com.cheung.tim.server.dto.PlayerDTO;
import com.cheung.tim.server.enums.GameStatus;
import com.cheung.tim.server.exception.BadRequestException;
import com.cheung.tim.server.exception.NotFoundException;
import com.cheung.tim.server.service.GameService;
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
import org.springframework.web.util.NestedServletException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = GameController.class)
class GameControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    GameService gameService;

    @MockBean
    PlayerService playerService;

    @MockBean
    ModelMapper modelMapper;

    ArgumentCaptor playerDTOCapture = ArgumentCaptor.forClass(PlayerDTO.class);
    ArgumentCaptor lobbyNameCapture = ArgumentCaptor.forClass(String.class);

    @Test
    public void getGame_shouldReturn200() throws Exception {
        Player player = new Player("John Smith");
        Game game = new Game("test_lobby", player, GameStatus.OPEN);
        GameDTO gameDTO = getGameDTO();

        when(gameService.getGame(anyLong())).thenReturn(game);
        when(modelMapper.map(game, GameDTO.class)).thenReturn(gameDTO);

        String expectedJson = "{\n" +
                "   \"id\":null,\n" +
                "   \"createdAt\":null,\n" +
                "   \"lobbyName\":\"test_lobby\",\n" +
                "   \"host\":{\n" +
                "      \"username\":\"John Smith\"\n" +
                "   },\n" +
                "   \"guest\": null,\n" +
                "   \"gameStatus\":\"OPEN\"\n" +
                "}";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/game/1")
        ).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertThat(response.getStatus(), is(200));
        assertThat(response.getContentAsString(), sameJSONAs(expectedJson));
    }

    @Test
    public void getGame_shouldThrowNotFoundException()  throws Exception{
        when(gameService.getGame(anyLong())).thenThrow(new NotFoundException("not found"));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/game/1")).andReturn();
        MockHttpServletResponse response = result.getResponse();

        assertThat(response.getStatus(), is(404));
    }

    @Test
    public void createGame_shouldReturn200() throws Exception {
        Player player = new Player("John Smith");
        Game game = new Game("test_lobby", player, GameStatus.OPEN);
        GameDTO gameDTO = getGameDTO();

        when(gameService.createGame(any(PlayerDTO.class), anyString())).thenReturn(game);
        when(modelMapper.map(game, GameDTO.class)).thenReturn(gameDTO);
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
                "      \"username\":\"John Smith\"\n" +
                "   },\n" +
                "   \"guest\": null,\n" +
        "   \"gameStatus\":\"OPEN\"\n" +
                "}";

        verify(gameService).createGame((PlayerDTO) playerDTOCapture.capture(), (String) lobbyNameCapture.capture());
        assertThat(lobbyNameCapture.getAllValues().size(), is(1));

        assertThat(response.getStatus(), is(200));
        assertThat(response.getContentAsString(), sameJSONAs(expectedJson));
    }

    @Test
    public void createGame_shouldThrowBadRequestException() throws Exception {
        when(gameService.createGame(any(PlayerDTO.class), anyString())).thenThrow(new BadRequestException(""));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getCreateRequest())).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertThat(response.getStatus(), is(400));
    }

    @Test
    public void getGames_shouldReturn200() throws Exception {
        List<Game> games = new ArrayList();
        Player player = new Player("John Smith");
        Game game = new Game("test_lobby", player, GameStatus.OPEN);
        games.add(game);
        when(gameService.findOpenGames()).thenReturn(games);

        when(modelMapper.map(game, GameDTO.class)).thenReturn(getGameDTO());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/games").content("")).andReturn();

        MockHttpServletResponse response = result.getResponse();

        String expectedJson = "{\n" +
                "   \"numOpenGames\": 1,\n" +
                "   \"games\": [\n" +
                "      {\n" +
                "         \"id\": null,\n" +
                "         \"createdAt\": null,\n" +
                "         \"lobbyName\": \"test_lobby\",\n" +
                "         \"host\": {\n" +
                "            \"username\": \"John Smith\"\n" +
                "         },\n" +
                "         \"guest\": null,\n" +
                "         \"gameStatus\": \"OPEN\"\n" +
                "      }\n" +
                "   ]\n" +
                "}";

        assertThat(response.getStatus(), is(200));
        assertThat(response.getContentAsString(), sameJSONAs(expectedJson));
    }

    @Test
    public void joinGame_shouldReturn204() throws Exception {
        MvcResult result = performPatch("/join/1");

        MockHttpServletResponse response = result.getResponse();

        assertThat(response.getStatus(), is(204));
        assertThat(response.getContentAsString(), is(""));
    }

    @Test
    public void joinGame_shouldThrowBadRequestException() throws Exception{
        doThrow(new BadRequestException("")).when(gameService).joinGame(any(Long.class), any(PlayerDTO.class));

        MvcResult result = performPatch("/join/1");
        MockHttpServletResponse response = result.getResponse();

        assertThat(response.getStatus(), is(400));
    }

    @Test
    public void joinGame_shouldThrowNotFoundException() throws Exception{
        doThrow(new NotFoundException("not found")).when(gameService).joinGame(any(Long.class), any(PlayerDTO.class));

        MvcResult result = performPatch("/join/1");
        MockHttpServletResponse response = result.getResponse();

        assertThat(response.getStatus(), is(404));
    }

    @Test
    public void leaveGame_shouldReturn204() throws Exception {
        MvcResult result = performPatch("/leave/1");

        MockHttpServletResponse response = result.getResponse();

        assertThat(response.getStatus(), is(204));
        assertThat(response.getContentAsString(), is(""));
    }

    @Test
    public void leaveGame_shouldThrowBadRequestException() throws Exception{
        doThrow(new BadRequestException("bad request")).when(gameService).leaveGame(any(Long.class), any(PlayerDTO.class));

        MvcResult result = performPatch("/leave/1");
        MockHttpServletResponse response = result.getResponse();

        assertThat(response.getStatus(), is(400));
    }

    @Test
    public void leaveGame_shouldThrowNotFoundException() throws Exception{
        doThrow(new NotFoundException("not found")).when(gameService).leaveGame(any(Long.class), any(PlayerDTO.class));

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
        return gameDTO;
    }

    private String getCreateRequest() {
        return "{\n" +
                "  \"lobbyName\": \"test_lobby\",\n" +
                "  \"host\": {\n" +
                "    \"id\": \"40283481721d879601721d87b6350000\"\n" +
                "  }\n" +
                "}";
    }
}