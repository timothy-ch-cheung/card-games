package com.cheung.tim.server.controller;

import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.PrivatePlayerDTO;
import com.cheung.tim.server.dto.PublicPlayerDTO;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PlayerController.class)
class PlayerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PlayerService playerService;

    @MockBean
    ModelMapper modelMapper;

    ArgumentCaptor playerDTOCapture = ArgumentCaptor.forClass(PrivatePlayerDTO.class);
    ArgumentCaptor stringCapture = ArgumentCaptor.forClass(String.class);

    @Test
    void createPlayer_shouldReturn200() throws Exception {
        Player player = new Player("40283481721d879601721d87b6350000", "John Smith", "1dpwq1441pgj670g0wzx7vfluomyy5iq");
        when(playerService.createPlayer(any(PrivatePlayerDTO.class))).thenReturn(player);
        when(modelMapper.map(player, PrivatePlayerDTO.class)).thenReturn(new PrivatePlayerDTO("40283481721d879601721d87b6350000", "John Smith", "1dpwq1441pgj670g0wzx7vfluomyy5iq"));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/player")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"John Smith\"}")
        ).andReturn();

        MockHttpServletResponse response = result.getResponse();

        verify(playerService).createPlayer((PrivatePlayerDTO) playerDTOCapture.capture());
        assertThat(playerDTOCapture.getAllValues().size(), is(1));
        assertThat(((PrivatePlayerDTO) playerDTOCapture.getAllValues().get(0)).getUsername(), is("John Smith"));

        assertThat(response.getStatus(), is(200));
        assertThat(response.getContentAsString(), sameJSONAs("{\"username\":\"John Smith\",\"id\":\"40283481721d879601721d87b6350000\",\"key\":\"1dpwq1441pgj670g0wzx7vfluomyy5iq\"}"));
    }

    @Test
    void getPlayer_shouldReturn200() throws Exception {
        Player player = new Player("40283481721d879601721d87b6350000", "John Smith");
        when(playerService.findPlayerById("40283481721d879601721d87b6350000")).thenReturn(player);
        when(modelMapper.map(player, PublicPlayerDTO.class)).thenReturn(new PublicPlayerDTO("40283481721d879601721d87b6350000", "John Smith"));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/player/40283481721d879601721d87b6350000")
        ).andReturn();

        MockHttpServletResponse response = result.getResponse();

        verify(playerService).findPlayerById((String) stringCapture.capture());
        assertThat(stringCapture.getAllValues().size(), is(1));
        assertThat((stringCapture.getAllValues().get(0)), is("40283481721d879601721d87b6350000"));

        assertThat(response.getStatus(), is(200));
        assertThat(response.getContentAsString(), sameJSONAs("{\"id\":\"40283481721d879601721d87b6350000\",\"username\":\"John Smith\"}"));
    }
}