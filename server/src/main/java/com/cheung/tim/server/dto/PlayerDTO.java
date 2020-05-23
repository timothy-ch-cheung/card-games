package com.cheung.tim.server.dto;

import com.cheung.tim.server.domain.Player;
import lombok.Getter;
import lombok.Setter;

public class PlayerDTO extends PublicPlayerDTO {

    public PlayerDTO() {
    }

    public PlayerDTO(String id, String username) {
        super(username);
        this.id = id;
    }

    @Getter
    @Setter
    private String id;
}
