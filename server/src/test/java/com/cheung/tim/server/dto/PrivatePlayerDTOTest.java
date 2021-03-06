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

    @Test
    void equals_shouldReturnTrueWhenSameId() {
        PrivatePlayerDTO dtoOne = new PrivatePlayerDTO("12345678901234567890123456789012", "John");
        assertThat(dtoOne.equals(new PrivatePlayerDTO("12345678901234567890123456789012", "John")), is(true));
    }

    @Test
    void equals_shouldReturnTrueWhenSameIdButDifferentUsername() {
        PrivatePlayerDTO dtoOne = new PrivatePlayerDTO("12345678901234567890123456789012", "John");
        assertThat(dtoOne.equals(new PrivatePlayerDTO("12345678901234567890123456789012", "Jane")), is(true));
    }

    @Test
    void equals_shouldReturnTrueWhenSameIdButDifferentKey() {
        PrivatePlayerDTO dtoOne = new PrivatePlayerDTO("12345678901234567890123456789012", "John", "keyone");
        assertThat(dtoOne.equals(new PrivatePlayerDTO("12345678901234567890123456789012", "John", "keytwo")), is(true));
    }

    @Test
    void equals_shouldReturnFalseWhenDifferentId() {
        PrivatePlayerDTO dtoOne = new PrivatePlayerDTO("12345678901234567890123456789012", "John");
        assertThat(dtoOne.equals(new PrivatePlayerDTO("90123456789012345678901212345678", "Jane")), is(false));
    }
}