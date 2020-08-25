package com.cheung.tim.server.dto;

import lombok.Getter;
import lombok.Setter;

public class UpdateLobbyDTO {
    @Getter
    @Setter
    private Integer rounds;

    @Getter
    @Setter
    private PrivatePlayerDTO host;
}
