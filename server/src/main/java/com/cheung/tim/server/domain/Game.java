package com.cheung.tim.server.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "GAME")
public class Game extends BaseEntity {

    @Getter
    @Column(columnDefinition = "CHAR(32)", nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Id
    private String id;

    @Getter
    private Integer currentRound;

    @OneToOne(mappedBy = "game")
    @Getter
    private Lobby lobby;

    @Getter
    @Setter
    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY)
    private Set<PlayerGameData> playerGameData = new HashSet<>();

    @Getter
    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private Player winner = null;

    public void nextRound() {
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
