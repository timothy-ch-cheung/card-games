package com.cheung.tim.server.repository;

import com.cheung.tim.server.domain.Game;
import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.enums.GameStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GameRepository extends CrudRepository<Game, Long> {

    @Query("SELECT g FROM Game g INNER JOIN g.player1 p1 WHERE p1.userId = g.player1.userId AND g.gameStatus = :gameStatus")
    List<Game> findByGameStatus(GameStatus gameStatus);

    @Query("SELECT COUNT(g) FROM Game g INNER JOIN g.player1 p1 WHERE p1.userId = g.player1.userId AND g.player1 = :player")
    Long countByPlayer(Player player);
}
