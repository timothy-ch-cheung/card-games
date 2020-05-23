package com.cheung.tim.server.dto;

import com.cheung.tim.server.domain.Player;
import lombok.Getter;
import lombok.Setter;

public class PublicPlayerDTO {

    public PublicPlayerDTO() {
    }

    public PublicPlayerDTO(String username) {
        this.username = username;
    }

    @Getter
    @Setter
    private String username;

    public static PublicPlayerDTO convertToPublicPlayerDTO(Player player) {
        PublicPlayerDTO publicPlayerDTO = new PublicPlayerDTO();
        if (player != null && player.getUsername() != null) {
            publicPlayerDTO.setUsername(player.getUsername());
        }
        else {
            return null;
        }
        return publicPlayerDTO;
    }
}
