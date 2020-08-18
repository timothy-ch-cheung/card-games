package com.cheung.tim.server.dto;

import com.cheung.tim.server.domain.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class PublicPlayerDTO {

    public PublicPlayerDTO() {
    }

    public PublicPlayerDTO(String id, String username) {
        this.id = id;
        this.username = username;
    }

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String id;

    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PublicPlayerDTO otherPlayer = (PublicPlayerDTO) obj;
        return Objects.equals(id, otherPlayer.id);
    }

    public static PublicPlayerDTO convertToPublicPlayerDTO(Player player) {
        PublicPlayerDTO publicPlayerDTO = new PublicPlayerDTO();
        if (player != null && player.getUsername() != null) {
            publicPlayerDTO.setId(player.getUserId());
            publicPlayerDTO.setUsername(player.getUsername());
        } else {
            return null;
        }
        return publicPlayerDTO;
    }

    public static Set<PublicPlayerDTO> convertToPublicPlayerDTOSet(Set<Player> players) {
        return players.stream().map(PublicPlayerDTO::convertToPublicPlayerDTO).collect(toSet());
    }
}
