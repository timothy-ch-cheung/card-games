package com.cheung.tim.server.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

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
    private Set<PublicPlayerDTO> guests;

    @Getter
    @Setter
    private String gameStatus;

    @Getter
    @Setter
    private Integer maxPlayers;
}
