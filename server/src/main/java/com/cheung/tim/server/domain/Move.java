package com.cheung.tim.server.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "MOVES")
@RequiredArgsConstructor
public class Move extends BaseEntity {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long moveId;

    @Getter
    @Setter
    private Integer colNum;

    @Getter
    @Setter
    private Integer rowNum;

    @OneToOne
    @Getter
    @Setter
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne
    @Getter
    @Setter
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    public Move(Move move){
        this.moveId = move.moveId;
        this.colNum = move.colNum;
        this.rowNum = move.rowNum;
        this.player = move.player;
        this.game = move.game;

    }

    @Override
    public int hashCode() {
        return Objects.hash(moveId, colNum, rowNum, player, game);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Move otherMove = (Move) obj;
        return Objects.equals(moveId, otherMove.moveId);
    }
}
