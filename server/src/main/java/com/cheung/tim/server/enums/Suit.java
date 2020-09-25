package com.cheung.tim.server.enums;

import lombok.Getter;

public enum Suit {
    DIAMONDS("diamonds"),
    CLUBS("clubs"),
    HEARTS("hearts"),
    SPADES("spades");

    Suit(String name) {
        this.name = name;
    }

    @Getter
    private final String name;
}
