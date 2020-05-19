package com.cheung.tim.server.controller;

import com.cheung.tim.server.domain.Game;
import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.GameDTO;
import com.cheung.tim.server.dto.PlayerDTO;
import com.cheung.tim.server.enums.GameStatus;
import com.cheung.tim.server.exception.BadRequestException;
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

    ArgumentCaptor gameDTOCapture = ArgumentCaptor.forClass(GameDTO.class);

    @Test
    public void createGame_shouldReturn200() throws Exception {
        Player player = new Player("John Smith");
        Game game = new Game("test_lobby", player, GameStatus.OPEN);
        GameDTO gameDTO = getGameDTO();

        when(gameService.createGame(any(GameDTO.class))).thenReturn(game);
        when(modelMapper.map(game, GameDTO.class)).thenReturn(gameDTO);
        when(modelMapper.map(player, PlayerDTO.class)).thenReturn(new PlayerDTO("40283481721d879601721d87b6350000", "John Smith"));

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
                "      \"id\":\"40283481721d879601721d87b6350000\",\n" +
                "      \"username\":\"John Smith\"\n" +
                "   },\n" +
                "   \"gameStatus\":\"OPEN\"\n" +
                "}";

        verify(gameService).createGame((GameDTO) gameDTOCapture.capture());
        assertThat(gameDTOCapture.getAllValues().size(), is(1));
        GameDTO mappedDTO = (GameDTO) gameDTOCapture.getAllValues().get(0);

        assertThat(mappedDTO.getLobbyName(), is("test_lobby"));
        assertThat(mappedDTO.getHost().getId(), is("40283481721d879601721d87b6350000"));

        assertThat(response.getStatus(), is(200));
        assertThat(response.getContentAsString(), sameJSONAs(expectedJson));
    }

    @Test
    public void createGame_shouldThrowBadRequestException() throws Exception {
        when(gameService.createGame(any(GameDTO.class))).thenThrow(new BadRequestException("bad request"));

        NestedServletException nestedServletException = assertThrows(NestedServletException.class, () -> {
                    mockMvc.perform(MockMvcRequestBuilders
                            .post("/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(getCreateRequest())
                    ).andReturn();
                }
        );
        assertThat(nestedServletException.getCause(), instanceOf(BadRequestException.class));
        assertThat(nestedServletException.getCause().getMessage(), is("bad request"));
    }

    @Test
    public void getGames_shouldReturn200() throws Exception {
        List<Game> games = new ArrayList();
        Player player = new Player("John Smith");
        Game game = new Game("test_lobby", player, GameStatus.OPEN);
        games.add(game);
        when(gameService.findOpenGames()).thenReturn(games);

        when(modelMapper.map(game, GameDTO.class)).thenReturn(getGameDTO());
        when(modelMapper.map(player, PlayerDTO.class)).thenReturn(new PlayerDTO("40283481721d879601721d87b6350000", "John Smith"));

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
                "            \"id\": \"40283481721d879601721d87b6350000\",\n" +
                "            \"username\": \"John Smith\"\n" +
                "         },\n" +
                "         \"gameStatus\": \"OPEN\"\n" +
                "      }\n" +
                "   ]\n" +
                "}";

        assertThat(response.getStatus(), is(200));
        assertThat(response.getContentAsString(), sameJSONAs(expectedJson));
    }

    @Test
    public void joinGame_shouldReturn204() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .patch("/join/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"40283481721d879601721d87b6350000\"}")
        ).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertThat(response.getStatus(), is(204));
        assertThat(response.getContentAsString(), is(""));
    }

    @Test
    public void joinGame_shouldThrowBadRequestException() throws Exception {
        doThrow(new BadRequestException("bad request")).when(gameService).joinGame(any(Long.class), any(PlayerDTO.class));

        NestedServletException nestedServletException = assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders
                    .patch("/join/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"id\":\"40283481721d879601721d87b6350000\"}")
            ).andReturn();
        });

        assertThat(nestedServletException.getCause(), instanceOf(BadRequestException.class));
        assertThat(nestedServletException.getCause().getMessage(), is("bad request"));
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