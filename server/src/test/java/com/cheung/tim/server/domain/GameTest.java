package com.cheung.tim.server.domain;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class GameTest {

    private Game game;

    @BeforeEach
    public void setup() throws IllegalAccessException {
        this.game = createGame();
    }

    @Test
    void hashCode_returnsSameHashForSameObject() {
        assertThat(game.hashCode(), is(game.hashCode()));
    }

    @Test
    void hashCode_returnsSameHashForEqualObject() throws IllegalAccessException {
        Game otherGame = createGame();
        assertThat(otherGame.hashCode(), is(game.hashCode()));
    }

    @Test
    void equals_returnsTrueForEqualGame() throws IllegalAccessException {
        assertThat(game.equals(createGame()), is(true));
    }

    @Test
    void equals_returnsTrueForSameObject() {
        assertThat(game.equals(game), is(true));
    }

    @Test
    void equals_returnsFalseForNull() {
        assertThat(game.equals(null), is(false));
    }

    @Test
    void equals_returnsFalseForDifferentClass() {
        assertThat(game.equals(new NullPointerException()), is(false));
    }

    @Test
    void equals_returnsFalseForDifferentRoundNum() throws IllegalAccessException {
        Game otherGame = createGame();
        otherGame.nextRound();
        assertThat(game.equals(otherGame), is(false));
    }

    @Test
    void nextRound_incrementsRound() throws Exception {
        Game game = createGame();
        assertThat(game.getCurrentRound(), is(1));
        game.nextRound();
        assertThat(game.getCurrentRound(), is(2));
    }

    private Game createGame() throws IllegalAccessException {
        Game game = new Game();
        FieldUtils.writeField(game, "id", "1234567890123456789012", true);
        game.onCreate();
        return game;
    }
}