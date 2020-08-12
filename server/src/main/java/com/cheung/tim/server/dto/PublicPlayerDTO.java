package com.cheung.tim.server.dto;

import com.cheung.tim.server.domain.Player;
import lombok.Getter;
import lombok.Setter;

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
}
