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

class GameTest {

    Game game;

    @BeforeEach
    public void setup() {
        this.game = createGame();
    }

    @Test
    void getMoves_returnsCopy() {
        List<Move> moves = this.game.getMoves();
        assertThat(moves.size(), is(0));
        moves.add(new Move());
        assertThat(this.game.getMoves().size(), is(0));
    }

    @Test
    void addMove_putsMoveInList() throws Exception {
        Move move = new Move();
        this.game.addMove(move);
        assertThat(this.game.getMoves().size(), is(1));
        assertThat(((List<Move>) FieldUtils.readField(this.game, "moves", true)).get(0), sameInstance(move));
    }

    @Test
    void hashCode_returnsSameHashForSameObject() {
        assertThat(game.hashCode(), is(game.hashCode()));
    }

    @Test
    void equals_returnsTrueForEqualGame() {
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
    void equals_returnsFalseForDifferentLobbyName() {
        Game otherGame = createGame();
        otherGame.setLobbyName("Other Lobby");
        assertThat(game.equals(otherGame), is(false));
    }

    @Test
    void equals_returnsFalseForDifferentPlayer1() {
        Game otherGame = createGame();
        otherGame.setPlayer1(mock(Player.class));
        assertThat(game.equals(otherGame), is(false));
    }

    @Test
    void equals_returnsFalseForDifferentPlayer2() {
        Game otherGame = createGame();
        otherGame.setPlayer2(mock(Player.class));
        assertThat(game.equals(otherGame), is(false));
    }

    @Test
    void equals_returnsFalseForDifferentGameStatus() {
        Game otherGame = createGame();
        otherGame.setGameStatus(GameStatus.DELETED);
        assertThat(game.equals(otherGame), is(false));
    }

    @Test
    void equals_returnsFalseForDifferentMoves() {
        Game otherGame = createGame();
        otherGame.addMove(new Move());
        assertThat(game.equals(otherGame), is(false));
    }

    private Game createGame() {
        return new Game("test_lobby", new Player("John Smith"), GameStatus.OPEN);
    }

}