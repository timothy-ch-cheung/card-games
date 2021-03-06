package com.cheung.tim.server.domain;

import com.cheung.tim.server.dto.PrivatePlayerDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;

@Entity
@Table(name = "PLAYERS")
@RequiredArgsConstructor
public class Player extends BaseEntity {

    public Player(String username) {
        this.username = username;
    }

    public Player(String id, String username) {
        this.userId = id;
        this.username = username;
    }

    public Player(String id, String username, String key) {
        this(id, username);
        this.key = key;
    }

    public Player(Player player) {
        this(player.getUserId(), player.getUsername(), player.getKey());
        this.currentLobby = player.getCurrentLobby();
    }

    @Getter
    @Column(name = "user_id", columnDefinition = "CHAR(32)", nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Id
    private String userId;

    @Column(name = "key")
    @Getter
    @Setter
    private String key;

    @Column(name = "username", nullable = false)
    @Getter
    @Setter
    private String username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lobby_id")
    @Getter
    @Setter
    Lobby currentLobby;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @PrePersist
    protected void onCreate() {
        this.key = randomAlphanumeric(32);
    }

    public boolean equalDTO(PrivatePlayerDTO privatePlayerDTO) {
        return privatePlayerDTO != null && this.userId.equals(privatePlayerDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Player otherPlayer = (Player) obj;
        return Objects.equals(userId, otherPlayer.userId);
    }
}
