package com.cheung.tim.server.repository;

import com.cheung.tim.server.domain.Player;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<Player, Long> {
}
