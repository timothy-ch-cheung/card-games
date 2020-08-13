package com.cheung.tim.server.dto;

import com.cheung.tim.server.domain.Player;
import org.junit.jupiter.api.Test;

import static com.cheung.tim.server.dto.PrivatePlayerDTO.convertToPrivatePlayerDTO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

class PrivatePlayerDTOTest {

    @Test
    void convertToPrivatePlayerDTOShowsKey() {
        Player player = new Player("12345678901234567890123456789012", "John", "keykeykeykeykeykeykeykeykeykeyke");
        PrivatePlayerDTO dto = convertToPrivatePlayerDTO(player);
        assertThat(dto.getUsername(), is("John"));
        assertThat(dto.getId(), is("12345678901234567890123456789012"));
        assertThat(dto.getKey(), is("keykeykeykeykeykeykeykeykeykeyke"));
    }

    @Test
    void convertToPrivatePlayerDTOReturnsNullWhenUserIdNull() {
        Player player = new Player(null, "John", "keykeykeykeykeykeykeykeykeykeyke");
        PrivatePlayerDTO dto = convertToPrivatePlayerDTO(player);
        assertThat(dto, is(nullValue()));
    }
}