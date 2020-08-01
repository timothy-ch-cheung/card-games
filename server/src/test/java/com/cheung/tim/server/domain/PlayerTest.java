package com.cheung.tim.server.domain;

import com.cheung.tim.server.dto.PlayerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class PlayerTest {

    Player player;

    @BeforeEach
    void setup() {
        this.player = createPlayer();
    }

    @Test
    void hashCode_returnsSameHashForSameObject() {
        assertThat(player.hashCode(), is(player.hashCode()));
    }

    @Test
    void equals_returnsTrueForEqualGame() {
        assertThat(player.equals(createPlayer()), is(true));
    }

    @Test
    void equals_returnsTrueForDifferentUsernameButSameId() {
        Player playerNameChange = createPlayer();
        playerNameChange.setUsername("Jane");
        assertThat(player.equals(playerNameChange), is(true));
    }

    @Test
    void equals_returnsTrueForSameObject() {
        assertThat(player.equals(player), is(true));
    }

    @Test
    void equals_returnsFalseForNull() {
        assertThat(player.equals(null), is(false));
    }

    @Test
    void equals_returnsFalseForDifferentClass() {
        assertThat(player.equals(new NullPointerException()), is(false));
    }

    @Test
    void equals_returnsFalseDifferentId() {
        Player otherPlayer = new Player("11111111111111111111111122222222", "John");
        assertThat(player.equals(otherPlayer), is(false));
    }

    @Test
    void equalDTO_returnsTrueForSameId() {
        PlayerDTO dto = new PlayerDTO("11111111111111111111111111111111", "John");
        assertThat(player.equalDTO(dto), is(true));
    }

    @Test
    void equalDTO_returnsFalseForDifferentId() {
        PlayerDTO dto = new PlayerDTO("11111111111111111111111122222222", "John");
        assertThat(player.equalDTO(dto), is(false));
    }

    private Player createPlayer() {
        return new Player("11111111111111111111111111111111", "John");
    }
}