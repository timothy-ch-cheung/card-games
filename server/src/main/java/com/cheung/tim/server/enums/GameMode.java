package com.cheung.tim.server.enums;

import com.cheung.tim.server.domain.Lobby;
import lombok.Getter;

import static com.cheung.tim.server.enums.RoundIncrement.ONE;
import static com.cheung.tim.server.enums.RoundIncrement.PLAYER;

public enum GameMode {
    MATCH_TWO("Match Two", 2, 4, PLAYER, true),
    CHOICE_POKER("Choice Poker", 2, 6, ONE, false);

    GameMode(String name, Integer minPlayers, Integer maxPlayers, RoundIncrement increment, Boolean enabled) {
        this.name = name;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.roundIncrement = increment;
        this.enabled = enabled;
        if (PLAYER.equals(increment)) {
            this.initialRounds = minPlayers;
        } else {
            this.initialRounds = 1;
        }
    }

    private final String name;

    private final Integer minPlayers;

    private final Integer maxPlayers;

    private final RoundIncrement roundIncrement;

    private final Boolean enabled;

    @Getter
    private final Integer initialRounds;

    public boolean isEnabled() {
        return this.enabled;
    }

    public static GameMode getEnum(String mode) {
        try {
            return valueOf(mode);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean isValidRounds(Lobby lobby, Integer rounds) {
        if (rounds <= 0) {
            return false;
        }
        if (this.roundIncrement == PLAYER) {
            int playersInLobby = lobby.getGuests().size() + 1;
            playersInLobby = playersInLobby < this.minPlayers ? this.minPlayers : playersInLobby;
            return rounds % playersInLobby == 0;
        }
        return true;
    }
}
