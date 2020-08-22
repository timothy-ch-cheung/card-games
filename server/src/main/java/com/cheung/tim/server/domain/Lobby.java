package com.cheung.tim.server.domain;

import com.cheung.tim.server.enums.GameMode;
import com.cheung.tim.server.enums.GameStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "LOBBY")
public class Lobby extends BaseEntity {

    public Lobby() {
    }

    public Lobby(String lobbyName, Player host, GameStatus status, Integer maxPlayers, GameMode gameMode) {
        this.lobbyName = lobbyName;
        this.host = host;
        this.gameStatus = status;
        this.maxPlayers = maxPlayers;
        this.gameMode = gameMode;
        this.rounds = gameMode.getInitialRounds();
    }


    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lobbyId;

    @Getter
    @Setter
    private String lobbyName;

    @Getter
    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", unique = true)
    private Game game;

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
    @Enumerated(EnumType.STRING)
    private GameMode gameMode;

    @Getter
    @Setter
    @NotNull
    private Integer maxPlayers;

    @Getter
    @Setter
    private Integer rounds;

    public Set<Player> getGuests() {
        return Collections.unmodifiableSet(this.guests);
    }

    public void addGuest(Player player) {
        this.guests.add(player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lobbyId, lobbyName, host, guests, gameStatus);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Lobby otherLobby = (Lobby) obj;
        return Objects.equals(lobbyId, otherLobby.lobbyId) &&
                Objects.equals(lobbyName, otherLobby.lobbyName) &&
                Objects.equals(host, otherLobby.host) &&
                Objects.equals(guests, otherLobby.guests) &&
                Objects.equals(gameStatus, otherLobby.gameStatus);
    }
}
