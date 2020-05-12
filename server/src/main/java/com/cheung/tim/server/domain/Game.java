package com.cheung.tim.server.domain;

import com.cheung.tim.server.enums.GameStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GAME")
public class Game {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    @Getter
    @Setter
    private String lobbyName;

    @Getter
    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Player player1;

    @OneToOne
    @Getter
    @Setter
    @JoinColumn(name = "second_player_id")
    private Player player2;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private GameStatus gameStatus;

    @OneToMany
    private List<Move> moves;

    public List<Move> getMoves() {
        List<Move> movesCopy = new ArrayList<>();
        for (Move m : this.moves) {
            movesCopy.add((Move) m.clone());
        }
        return movesCopy;
    }

    public void addMove(Move move) {
        this.moves.add(move);
    }
}
