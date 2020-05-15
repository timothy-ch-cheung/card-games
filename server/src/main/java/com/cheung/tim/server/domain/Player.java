package com.cheung.tim.server.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "PLAYERS")
@RequiredArgsConstructor
public class Player extends BaseEntity {

    public Player (String username){
        this.username = username;
    }

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false)
    @Getter
    @Setter
    private String username;
}
