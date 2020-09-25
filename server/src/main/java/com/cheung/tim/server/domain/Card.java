package com.cheung.tim.server.domain;

import com.cheung.tim.server.enums.Rank;
import com.cheung.tim.server.enums.Suit;
import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

public class Card {

    @Getter
    @Column(columnDefinition = "CHAR(32)", nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Id
    private String id;

    @Getter
    @Enumerated(EnumType.STRING)
    private Suit suit;

    @Getter
    @Enumerated(EnumType.STRING)
    private Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    @Override
    public int hashCode() {
        return Objects.hash(suit, rank);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Card otherCard = (Card) obj;
        return Objects.equals(suit, otherCard.suit) &&
                Objects.equals(rank, otherCard.rank);
    }
}
