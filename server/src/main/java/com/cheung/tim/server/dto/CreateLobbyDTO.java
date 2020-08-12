package com.cheung.tim.server.dto;

import lombok.Getter;
import lombok.Setter;

public class CreateLobbyDTO {

    @Getter
    @Setter
    private String lobbyName;

    @Getter
    @Setter
    private PrivatePlayerDTO host;
}
