package com.cheung.tim.server.repository;

import com.cheung.tim.server.domain.Game;
import com.cheung.tim.server.enums.GameStatus;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface GameRepository extends Repository<Game, Long> {

    List<Game> findByGameStatus(GameStatus gameStatus);
}
