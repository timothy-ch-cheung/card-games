package com.cheung.tim.server.dto;

import com.cheung.tim.server.domain.Player;
import org.junit.jupiter.api.Test;

import static com.cheung.tim.server.dto.PublicPlayerDTO.convertToPublicPlayerDTO;
import static org.hamcrest.MatcherAssert.assertThat;
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
}