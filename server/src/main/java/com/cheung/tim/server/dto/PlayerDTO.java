package com.cheung.tim.server.dto;

import lombok.Getter;
import lombok.Setter;

public class PlayerDTO {

    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String createdAt;

    @Getter
    @Setter
    private String username;
}
