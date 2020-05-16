package com.cheung.tim.server.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "PLAYERS")
@RequiredArgsConstructor
public class Player extends BaseEntity {

    public Player (String username){
        this.username = username;
    }

    @Getter
    @Column(name = "user_id", columnDefinition = "CHAR(32)")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Id
    private String userId;

    @Column(name = "username", nullable = false)
    @Getter
    @Setter
    private String username;
}
