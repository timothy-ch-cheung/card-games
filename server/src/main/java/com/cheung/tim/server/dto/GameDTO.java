package com.cheung.tim.server.dto;

import lombok.Getter;
import lombok.Setter;

public class GameDTO {

    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String createdAt;

    @Getter
    @Setter
    private String lobbyName;

    @Getter
    @Setter
    private PublicPlayerDTO host;

    @Getter
    @Setter
    private PublicPlayerDTO guest;

    @Getter
    @Setter
    private String gameStatus;
}
