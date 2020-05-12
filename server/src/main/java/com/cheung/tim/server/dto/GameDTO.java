package com.cheung.tim.server.dto;

import lombok.Getter;
import lombok.Setter;

public class GameDTO {

    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String lobbyName;

    @Getter
    @Setter
    private String host;
}
