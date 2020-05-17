package com.cheung.tim.server.domain;

import com.cheung.tim.server.enums.GameStatus;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsSame.sameInstance;

class GameTest {

    Game game;

    @BeforeEach
    public void setup() {
        this.game = new Game("test_lobby", new Player("John Smith"), GameStatus.OPEN);
    }

    @Test
    public void getMoves_returnsCopy() {
        List<Move> moves = this.game.getMoves();
        assertThat(moves.size(), is(0));
        moves.add(new Move());
        assertThat(this.game.getMoves().size(), is(0));
    }

    @Test
    public void addMove() throws Exception {
        Move move = new Move();
        this.game.addMove(move);
        assertThat(this.game.getMoves().size(), is(1));
        assertThat(((List<Move>) FieldUtils.readField(this.game, "moves", true)).get(0), sameInstance(move));
    }

}