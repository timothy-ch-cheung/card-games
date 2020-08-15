package com.cheung.tim.server.dto;

import com.cheung.tim.server.domain.Player;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.cheung.tim.server.dto.PublicPlayerDTO.convertToPublicPlayerDTO;
import static com.cheung.tim.server.dto.PublicPlayerDTO.convertToPublicPlayerDTOSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNull;

class PublicPlayerDTOTest {

    @Test
    void shouldReturnNullIfNoUsername() {
        Player player = new Player();
        PublicPlayerDTO dto = convertToPublicPlayerDTO(player);
        assertNull(dto);
    }

    @Test
    void shouldReturnNullIfPlayerNull() {
        PublicPlayerDTO dto = convertToPublicPlayerDTO(null);
        assertNull(dto);
    }

    @Test
    void shouldReturnDTOWhenUsernamePresent() {
        Player player = new Player("40283481721d879601721d87b6350000", "John Smith");
        PublicPlayerDTO dto = convertToPublicPlayerDTO(player);
        assertThat(dto.getUsername(), is("John Smith"));
    }

    @Test
    void shouldReturnDTOSet() {
        Player player1 = new Player("40283481721d879601721d87b6350000", "John Smith");
        Player player2 = new Player("7b635000040283481721d879601721d8", "Jane Smith");
        Set players = new HashSet<>(Arrays.asList(player1,player2));
        Set<PublicPlayerDTO> playersDTO = convertToPublicPlayerDTOSet(players);

        assertThat(playersDTO, hasSize(2));
        assertThat(playersDTO, hasItem(new PublicPlayerDTO("40283481721d879601721d87b6350000","John Smith")));
        assertThat(playersDTO, hasItem(new PublicPlayerDTO("7b635000040283481721d879601721d8","Jane Smith")));
    }
}