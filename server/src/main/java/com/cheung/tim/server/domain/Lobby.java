package com.cheung.tim.server.domain;

import com.cheung.tim.server.enums.GameStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
@Table(name = "GAME")
public class Lobby extends BaseEntity {

    public Lobby() {
    }

    public Lobby(String lobbyName, Player host, GameStatus status, Integer maxPlayers) {
        this.lobbyName = lobbyName;
        this.host = host;
        this.gameStatus = status;
        this.maxPlayers = maxPlayers;
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
    private Player host;

    @OneToMany(
            mappedBy = "currentLobby",
            cascade = CascadeType.PERSIST,
            fetch = FetchType.LAZY)
    private Set<Player> guests = new HashSet<>();
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private GameStatus gameStatus;

    @Getter
    @Setter
    @NotNull
    private Integer maxPlayers;

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

    public Set<Player> getGuests() {
        return Collections.unmodifiableSet(this.guests);
    }

    public void addGuest(Player player) {
        this.guests.add(player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId, lobbyName, host, guests, gameStatus, moves);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Lobby otherPlayer = (Lobby) obj;
        return Objects.equals(gameId, otherPlayer.gameId) &&
                Objects.equals(lobbyName, otherPlayer.lobbyName) &&
                Objects.equals(host, otherPlayer.host) &&
                Objects.equals(guests, otherPlayer.guests) &&
                Objects.equals(gameStatus, otherPlayer.gameStatus) &&
                Objects.equals(moves, otherPlayer.moves);
    }
}
