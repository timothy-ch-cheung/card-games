package com.cheung.tim.server.controller;

import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.PlayerDTO;
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

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PlayerController.class)
class PlayerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PlayerService playerService;

    @MockBean
    ModelMapper modelMapper;

    ArgumentCaptor playerDTOCapture = ArgumentCaptor.forClass(PlayerDTO.class);

    @Test
    public void createPlayer_shouldReturn200() throws Exception {
        Player player = new Player("40283481721d879601721d87b6350000", "John Smith");
        when(playerService.createPlayer(any(PlayerDTO.class))).thenReturn(player);
        when(modelMapper.map(player, PlayerDTO.class)).thenReturn(new PlayerDTO("40283481721d879601721d87b6350000", "John Smith"));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/player")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"John Smith\"}")
        ).andReturn();

        MockHttpServletResponse response = result.getResponse();

        verify(playerService).createPlayer((PlayerDTO) playerDTOCapture.capture());
        assertThat(playerDTOCapture.getAllValues().size(), is(1));
        assertThat(((PlayerDTO) playerDTOCapture.getAllValues().get(0)).getUsername(), is("John Smith"));

        assertThat(response.getStatus(), is(200));
        assertThat(response.getContentAsString(), is("{\"id\":\"40283481721d879601721d87b6350000\",\"username\":\"John Smith\"}"));
    }
}