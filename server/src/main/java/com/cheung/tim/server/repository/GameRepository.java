package com.cheung.tim.server.repository;

import com.cheung.tim.server.domain.Game;
import com.cheung.tim.server.enums.GameStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface GameRepository extends Repository<Game, Long> {

    @Query("SELECT g FROM Game g INNER JOIN g.player1 p1 WHERE p1.userId = g.player1.userId AND g.gameStatus = :gameStatus")
    List<Game> findByGameStatus(GameStatus gameStatus);
}
