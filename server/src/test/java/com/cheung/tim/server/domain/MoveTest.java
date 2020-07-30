package com.cheung.tim.server.domain;

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
        Game game = mock(Game.class);
        otherMove.setGame(game);
        otherMove.setPlayer(player);
        move.setGame(game);
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
    void equals_returnsFalseForMoveWithDifferentCoordinate() {
        Move otherMove = createMove();
        otherMove.setColNum(0);
        otherMove.setRowNum(5);
        assertThat(move.equals(otherMove), is(false));
    }

    private Move createMove() {
        Move move = new Move();
        move.setColNum(1);
        move.setRowNum(2);
        move.setPlayer(mock(Player.class));
        move.setGame(mock(Game.class));
        return move;
    }
}