package com.cheung.tim.server.repository;

import com.cheung.tim.server.domain.Game;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, Long> {
}
