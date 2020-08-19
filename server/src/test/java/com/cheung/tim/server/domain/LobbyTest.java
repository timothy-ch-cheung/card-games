package com.cheung.tim.server.domain;

import com.cheung.tim.server.enums.GameStatus;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

class LobbyTest {

    Lobby lobby;

    @BeforeEach
    public void setup() {
        this.lobby = createLobby();
    }

    @Test
    void hashCode_returnsSameHashForSameObject() {
        assertThat(lobby.hashCode(), is(lobby.hashCode()));
    }

    @Test
    void equals_returnsTrueForEqualGame() {
        assertThat(lobby.equals(createLobby()), is(true));
    }

    @Test
    void equals_returnsTrueForSameObject() {
        assertThat(lobby.equals(lobby), is(true));
    }

    @Test
    void equals_returnsFalseForNull() {
        assertThat(lobby.equals(null), is(false));
    }

    @Test
    void equals_returnsFalseForDifferentClass() {
        assertThat(lobby.equals(new NullPointerException()), is(false));
    }

    @Test
    void equals_returnsFalseForDifferentLobbyName() {
        Lobby otherLobby = createLobby();
        otherLobby.setLobbyName("Other Lobby");
        assertThat(lobby.equals(otherLobby), is(false));
    }

    @Test
    void equals_returnsFalseForDifferentGameId() throws Exception {
        Lobby otherLobby = createLobby();
        FieldUtils.writeField(otherLobby, "lobbyId", new Long(999), true);
        assertThat(lobby.equals(otherLobby), is(false));
    }

    @Test
    void equals_returnsFalseForDifferentHost() {
        Lobby otherLobby = createLobby();
        otherLobby.setHost(mock(Player.class));
        assertThat(lobby.equals(otherLobby), is(false));
    }

    @Test
    void equals_returnsFalseForDifferentGuests() {
        Lobby otherLobby = createLobby();
        otherLobby.addGuest(new Player());
        assertThat(lobby.equals(otherLobby), is(false));
    }

    @Test
    void equals_returnsFalseForDifferentGameStatus() {
        Lobby otherLobby = createLobby();
        otherLobby.setGameStatus(GameStatus.DELETED);
        assertThat(lobby.equals(otherLobby), is(false));
    }

    private Lobby createLobby() {
        return new Lobby("test_lobby", new Player("John Smith"), GameStatus.OPEN, 2);
    }

}