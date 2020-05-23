package com.cheung.tim.server.dto;

import lombok.Data;

@Data
public class CreateLobbyDTO {

    private String lobbyName;
    private PlayerDTO host;
}
