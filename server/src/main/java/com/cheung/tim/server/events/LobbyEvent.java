package com.cheung.tim.server.events;

import com.cheung.tim.server.domain.Lobby;
import org.springframework.context.ApplicationEvent;

public class LobbyEvent extends ApplicationEvent {

    public LobbyEvent(Lobby source) {
        super(source);
    }
}
