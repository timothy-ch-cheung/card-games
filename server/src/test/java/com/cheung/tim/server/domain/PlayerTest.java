package com.cheung.tim.server.domain;

import com.cheung.tim.server.dto.PrivatePlayerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
        PrivatePlayerDTO dto = new PrivatePlayerDTO("11111111111111111111111111111111", "John");
        assertThat(player.equalDTO(dto), is(true));
    }

    @Test
    void equalDTO_returnsFalseForDifferentId() {
        PrivatePlayerDTO dto = new PrivatePlayerDTO("11111111111111111111111122222222", "John");
        assertThat(player.equalDTO(dto), is(false));
    }

    @Test
    void equalDTO_returnsFalseForNullDTO() {
        assertThat(player.equalDTO(null), is(false));
    }

    @Test
    void setContains_returnsTrueWhenNewlyCreatedObject() {
        Set<Player> players  = new HashSet<>(Arrays.asList(new Player("123", "John"),new Player("321", "Jane")));
        assertThat(players.contains(new Player("123", "John")), is(true));
    }

    @Test
    void copyConstructorReturnsCopy() {
        Player player = new Player("123", "John", "key");
        Player playerCopy = new Player(player);
        assertThat(player.equals(playerCopy), is(true));
        assertThat(player == playerCopy, is(false));
    }

    private Player createPlayer() {
        return new Player("11111111111111111111111111111111", "John");
    }
}