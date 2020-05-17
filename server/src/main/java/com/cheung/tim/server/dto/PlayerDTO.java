package com.cheung.tim.server.dto;

import lombok.Getter;
import lombok.Setter;

public class PlayerDTO {

    public PlayerDTO() {
    }

    public PlayerDTO(String id, String username) {
        this.id = id;
        this.username = username;
    }

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String username;
}
