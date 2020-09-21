package com.cheung.tim.server.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
public class PlayerGameData extends BaseEntity {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @Getter
    @Setter
    private int score;
}
