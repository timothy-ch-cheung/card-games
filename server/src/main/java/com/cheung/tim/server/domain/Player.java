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
    @JoinColumn(name = "game_id")
    @Getter
    @Setter
    Game currentGame;

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
