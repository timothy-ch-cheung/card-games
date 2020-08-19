package com.cheung.tim.server.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import java.util.Objects;

import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;

public class Game extends BaseEntity {

    @Getter
    @Column(name = "user_id", columnDefinition = "CHAR(32)", nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Id
    private String id;

    @Getter
    private Integer currentRound;

    public void nextRound(){
        this.currentRound += 1;
    }

    @PrePersist
    protected void onCreate() {
        this.currentRound = 1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, currentRound);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Game otherGame = (Game) obj;
        return Objects.equals(id, otherGame.id) &&
                Objects.equals(currentRound, otherGame.currentRound);
    }
}
