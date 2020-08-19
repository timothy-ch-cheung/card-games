package com.cheung.tim.server.domain;

import com.cheung.tim.server.enums.GameStatus;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

class MoveTest {

    Move move;

    @BeforeEach
    void setup() {
        this.move = createMove();
    }

    @Test
    void hashCode_returnsSameHashForSameObject() {
        assertThat(move.hashCode(), is(move.hashCode()));
    }

    @Test
    void equals_returnsTrueForEqualMoves() {
        Move otherMove = createMove();
        Player player = mock(Player.class);
        Lobby lobby = mock(Lobby.class);
        otherMove.setLobby(lobby);
        otherMove.setPlayer(player);
        move.setLobby(lobby);
        move.setPlayer(player);

        assertThat(otherMove.equals(move), is(true));
    }

    @Test
    void equals_returnsTrueForSameObject() {
        assertThat(move.equals(move), is(true));
    }

    @Test
    void equals_returnsFalseForNull() {
        assertThat(move.equals(null), is(false));
    }

    @Test
    void equals_returnsFalseForDifferentClass() {
        assertThat(move.equals(new NullPointerException()), is(false));
    }

    @Test
    void equals_returnsFalseForMoveWithDifferentColumnCoordinate() {
        Move otherMove = createMove();
        otherMove.setColNum(0);
        assertThat(move.equals(otherMove), is(false));
    }

    @Test
    void equals_returnsFalseForMoveWithDifferentRowCoordinate() {
        Move otherMove = createMove();
        otherMove.setRowNum(5);
        assertThat(move.equals(otherMove), is(false));
    }

    @Test
    void equals_returnsFalseForDifferentMoveId() throws Exception {
        Move otherMove = createMove();
        FieldUtils.writeField(otherMove, "moveId", new Long(999), true);
        assertThat(move.equals(otherMove), is(false));
    }

    @Test
    void equals_returnsFalseForDifferentGame() {
        Move otherMove = createMove();
        move.setLobby(new Lobby("Lobby 1", new Player(), GameStatus.OPEN, 2));
        otherMove.setLobby(new Lobby("Lobby 2", new Player(), GameStatus.OPEN, 2));
        assertThat(move.equals(otherMove), is(false));
    }

    @Test
    void equals_returnsFalseForDifferentPlayer() {
        Move otherMove = createMove();
        move.setPlayer(new Player("Player 1"));
        otherMove.setPlayer(new Player("Player 2"));
        assertThat(move.equals(otherMove), is(false));
    }

    @Test
    void move_constructorCopiesObject() {
        Move copiedMove = new Move(move);
        assertThat(copiedMove.getColNum(), is(move.getColNum()));
        assertThat(copiedMove.getRowNum(), is(move.getRowNum()));
        assertThat(copiedMove.getMoveId(), is(move.getMoveId()));
        assertThat(copiedMove.getCreatedAt(), is(move.getCreatedAt()));
        assertThat(copiedMove.getUpdatedAt(), is(move.getUpdatedAt()));
    }

    private Move createMove() {
        Move move = new Move();
        move.setColNum(1);
        move.setRowNum(2);
        move.setPlayer(mock(Player.class));
        move.setLobby(mock(Lobby.class));
        return move;
    }
}