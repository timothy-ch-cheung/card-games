package com.cheung.tim.server.domain;

import com.cheung.tim.server.enums.GameStatus;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.mockito.Mockito.mock;

class LobbyTest {

    Lobby lobby;

    @BeforeEach
    public void setup() {
        this.lobby = createGame();
    }

    @Test
    void getMoves_returnsCopy() {
        List<Move> moves = this.lobby.getMoves();
        assertThat(moves.size(), is(0));
        moves.add(new Move());
        assertThat(this.lobby.getMoves().size(), is(0));
    }

    @Test
    void addMove_putsMoveInList() throws Exception {
        Move move = new Move();
        this.lobby.addMove(move);
        assertThat(this.lobby.getMoves().size(), is(1));
        assertThat(((List<Move>) FieldUtils.readField(this.lobby, "moves", true)).get(0), sameInstance(move));
    }

    @Test
    void hashCode_returnsSameHashForSameObject() {
        assertThat(lobby.hashCode(), is(lobby.hashCode()));
    }

    @Test
    void equals_returnsTrueForEqualGame() {
        assertThat(lobby.equals(createGame()), is(true));
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
        Lobby otherLobby = createGame();
        otherLobby.setLobbyName("Other Lobby");
        assertThat(lobby.equals(otherLobby), is(false));
    }

    @Test
    void equals_returnsFalseForDifferentGameId() throws Exception {
        Lobby otherLobby = createGame();
        FieldUtils.writeField(otherLobby, "gameId", new Long(999), true);
        assertThat(lobby.equals(otherLobby), is(false));
    }

    @Test
    void equals_returnsFalseForDifferentHost() {
        Lobby otherLobby = createGame();
        otherLobby.setHost(mock(Player.class));
        assertThat(lobby.equals(otherLobby), is(false));
    }

    @Test
    void equals_returnsFalseForDifferentGuests() {
        Lobby otherLobby = createGame();
        otherLobby.addGuest(new Player());
        assertThat(lobby.equals(otherLobby), is(false));
    }

    @Test
    void equals_returnsFalseForDifferentGameStatus() {
        Lobby otherLobby = createGame();
        otherLobby.setGameStatus(GameStatus.DELETED);
        assertThat(lobby.equals(otherLobby), is(false));
    }

    @Test
    void equals_returnsFalseForDifferentMoves() {
        Lobby otherLobby = createGame();
        otherLobby.addMove(new Move());
        assertThat(lobby.equals(otherLobby), is(false));
    }

    private Lobby createGame() {
        return new Lobby("test_lobby", new Player("John Smith"), GameStatus.OPEN, 2);
    }

}