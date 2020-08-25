package com.cheung.tim.server.enums;

import com.cheung.tim.server.domain.Lobby;
import com.cheung.tim.server.domain.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.cheung.tim.server.enums.GameMode.MATCH_TWO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

class GameModeTest {

    private Lobby lobby;

    @BeforeEach
    void setup() {
        this.lobby = new Lobby();
    }

    @ParameterizedTest
    @ValueSource(strings = {"MATCH_TWO", "CHOICE_POKER"})
    void isValidRounds_shouldReturnTrueForPositive(String gameMode) {
        GameMode mode = GameMode.getEnum(gameMode);
        assertThat(mode.isValidRounds(lobby, 2), is(true));
    }

    @ParameterizedTest
    @ValueSource(strings = {"MATCH_TWO", "CHOICE_POKER"})
    void isValidRounds_shouldReturnFalseForZero(String gameMode) {
        GameMode mode = GameMode.getEnum(gameMode);
        assertThat(mode.isValidRounds(lobby, 0), is(false));
    }

    @ParameterizedTest
    @ValueSource(strings = {"MATCH_TWO", "CHOICE_POKER"})
    void isValidRounds_shouldReturnFalseForNegative(String gameMode) {
        GameMode mode = GameMode.getEnum(gameMode);
        assertThat(mode.isValidRounds(lobby, -2), is(false));
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 4, 6})
    void isValidRoundsMatchTwo_shouldAssumeMinimumPlayerCount(Integer rounds) {
        assertThat(MATCH_TWO.isValidRounds(lobby, rounds), is(true));
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 6, 9})
    void isValidRoundsMatchTwo_shouldReturnTrueWhenScalingWithPlayerCount(Integer rounds) {
        lobby.addGuest(new Player("123", "John"));
        lobby.addGuest(new Player("321", "Jane"));
        assertThat(MATCH_TWO.isValidRounds(lobby, rounds), is(true));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5})
    void isValidRoundsMatchTwo_shouldReturnFalseWhenNotAMultipleOfPlayerCount(Integer rounds) {
        lobby.addGuest(new Player());
        assertThat(MATCH_TWO.isValidRounds(lobby, rounds), is(false));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Match Two", "Choice Poker"})
    void getEnum_returnsNullIfInvalidString(String gameMode) {
        GameMode mode = GameMode.getEnum(gameMode);
        assertThat(mode, is(nullValue()));
    }
}