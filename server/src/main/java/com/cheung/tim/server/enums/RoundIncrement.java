package com.cheung.tim.server.enums;

public enum RoundIncrement {
    ONE("ONE"),
    PLAYER("PLAYER");

    private RoundIncrement(String increment) {
        this.increment = increment;
    }

    private final String increment;
}
