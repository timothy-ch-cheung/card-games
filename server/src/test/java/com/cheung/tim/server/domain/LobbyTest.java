package com.cheung.tim.server.domain;

import com.cheung.tim.server.enums.GameStatus;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.cheung.tim.server.enums.GameMode.MATCH_TWO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
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

    @Test
    void getHost_returnsCopy() {
        Lobby lobby = createLobby();
        Player host = lobby.getHost();
        host.setUsername("NEW_USERNAME");

        assertThat(lobby.getHost().getUsername(), is("John Smith"));
    }

    @Test
    void getGuests_returnsDeepCopy() {
        Lobby lobby = createLobby();
        Player player = new Player("ORIGINAL", "ORIGINAL", "ORIGINAL");
        lobby.addGuest(player);

        Set<Player> guestSetCopy = lobby.getGuests();
        ((Player) guestSetCopy.toArray()[0]).setUsername("NEW_USERNAME");
        assertThat(((Player) (lobby.getGuests().toArray()[0])).getUsername(), is("ORIGINAL"));
    }

    @Test
    void getHost_returnsNullIfHostIsNull() {
        Lobby lobby = new Lobby("test_lobby", null, GameStatus.OPEN, 2, MATCH_TWO);
        assertThat(lobby.getHost(), is(nullValue()));
    }

    private Lobby createLobby() {
        return new Lobby("test_lobby", new Player("John Smith"), GameStatus.OPEN, 2, MATCH_TWO);
    }

}