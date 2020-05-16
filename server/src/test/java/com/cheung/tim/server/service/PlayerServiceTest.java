package com.cheung.tim.server.service;

import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.PlayerDTO;
import com.cheung.tim.server.exception.NotFoundException;
import com.cheung.tim.server.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PlayerServiceTest {

    @Mock
    PlayerRepository playerRepository;

    PlayerService playerService;
    PlayerDTO playerDTO;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.playerService = new PlayerService(playerRepository);
        this.playerDTO = new PlayerDTO();
        this.playerDTO.setUsername("John Smith");
        this.playerDTO.setId("40283481721d879601721d87b6350000");
    }

    @Test
    public void createPlayer_shouldCreatePlayerSuccessfully() {
        when(playerRepository.save(any())).thenReturn(new Player());
        playerService.createPlayer(playerDTO);
        verify(playerRepository).save(any());
    }

    @Test
    public void findPlayerById_shouldFindPlayer() {
        when(playerRepository.findById(anyString())).thenReturn(Optional.of(new Player()));
        playerService.findPlayerById(playerDTO);
        verify(playerRepository, times(1)).findById("40283481721d879601721d87b6350000");
    }

    @Test
    public void findPlayerById_shouldThrowExceptionWhenPlayerNotFound() {
        when(playerRepository.findById(anyString())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            playerService.findPlayerById(playerDTO);
        });
        verify(playerRepository, times(1)).findById("40283481721d879601721d87b6350000");
        assertThat(exception.getMessage(), is("Player with id 40283481721d879601721d87b6350000 not found"));
    }
}