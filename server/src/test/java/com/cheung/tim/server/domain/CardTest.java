package com.cheung.tim.server.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.cheung.tim.server.enums.Rank.ACE;
import static com.cheung.tim.server.enums.Rank.KING;
import static com.cheung.tim.server.enums.Suit.CLUBS;
import static com.cheung.tim.server.enums.Suit.SPADES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class CardTest {

    Card card;

    @BeforeEach
    public void setup() {
        this.card = new Card(SPADES, ACE);
    }

    @Test
    void hashCode_returnsSameHashForSameObject() {
        assertThat(card.hashCode(), is(card.hashCode()));
    }

    @Test
    void hashCode_returnsSameHashForSameCard() {
        Card aceSpades = new Card(SPADES, ACE);
        assertThat(card.hashCode(), is(aceSpades.hashCode()));
    }

    @Test
    void equals_returnsTrueForSameObject() {
        assertThat(card.equals(card), is(true));
    }

    @Test
    void equals_returnsTrueForSameCard() {
        Card aceSpades = new Card(SPADES, ACE);
        assertThat(card.equals(aceSpades), is(true));
    }

    @Test
    void equals_returnsFalseForDifferentCard() {
        Card kingClubs = new Card(CLUBS, KING);
        assertThat(card.equals(kingClubs), is(false));
    }

    @Test
    void equals_returnsFalseForNull() {
        assertThat(card.equals(null), is(false));
    }

    @Test
    void equals_returnsFalseForDifferentClass() {
        assertThat(card.equals(new NullPointerException()), is(false));
    }
}