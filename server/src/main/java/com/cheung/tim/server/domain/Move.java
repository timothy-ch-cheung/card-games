package com.cheung.tim.server.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name ="MOVES")
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

    @Override
    public Object clone () {
        Move move = new Move();
        move.setColNum(this.colNum);
        move.setColNum(this.rowNum);
        return move;
    }
}
