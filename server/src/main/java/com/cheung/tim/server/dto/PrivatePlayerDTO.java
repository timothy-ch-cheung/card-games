package com.cheung.tim.server.dto;

import com.cheung.tim.server.domain.Player;
import lombok.Getter;
import lombok.Setter;

public class PrivatePlayerDTO extends PublicPlayerDTO {

    public PrivatePlayerDTO() {
    }

    public PrivatePlayerDTO(String id, String username) {
        super(id, username);
    }

    public PrivatePlayerDTO(String id, String username, String key) {
        this(id, username);
        this.key = key;
    }

    @Getter
    @Setter
    private String key;

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public static PrivatePlayerDTO convertToPrivatePlayerDTO(Player player) {
        PrivatePlayerDTO privatePlayerDTO = new PrivatePlayerDTO();
        if (player != null && player.getUserId() != null) {
            privatePlayerDTO.setId(player.getUserId());
            privatePlayerDTO.setUsername(player.getUsername());
            privatePlayerDTO.setKey(player.getKey());
        } else {
            return null;
        }
        return privatePlayerDTO;
    }
}
