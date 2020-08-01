package com.cheung.tim.server.domain;

import com.cheung.tim.server.enums.GameStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "GAME")
public class Game extends BaseEntity {

    public Game() {
    }

    public Game(String lobbyName, Player host, GameStatus status) {
        this.lobbyName = lobbyName;
        this.player1 = host;
        this.gameStatus = status;
    }

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
    @JoinColumn(name = "user_id", unique = true)
    private Player player1;

    @OneToOne
    @Getter
    @Setter
    @JoinColumn(name = "second_player_id", unique = true)
    private Player player2;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private GameStatus gameStatus;

    @OneToMany
    private List<Move> moves = new ArrayList<>();

    public List<Move> getMoves() {
        List<Move> movesCopy = new ArrayList<>();
        for (Move m : this.moves) {
            movesCopy.add(new Move(m));
        }
        return movesCopy;
    }

    public void addMove(Move move) {
        this.moves.add(move);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId, lobbyName, player1, player2, gameStatus, moves);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Game otherPlayer = (Game) obj;
        return Objects.equals(gameId, otherPlayer.gameId) &&
                Objects.equals(lobbyName, otherPlayer.lobbyName) &&
                Objects.equals(player1, otherPlayer.player1) &&
                Objects.equals(player2, otherPlayer.player2) &&
                Objects.equals(gameStatus, otherPlayer.gameStatus) &&
                Objects.equals(moves, otherPlayer.moves);
    }
}
