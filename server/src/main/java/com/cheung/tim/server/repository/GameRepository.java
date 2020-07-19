package com.cheung.tim.server.repository;

import com.cheung.tim.server.domain.Game;
import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.enums.GameStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface GameRepository extends CrudRepository<Game, Long> {

    @Query("SELECT g FROM Game g INNER JOIN g.player1 p1 WHERE p1.userId = g.player1.userId AND g.gameStatus = :gameStatus")
    List<Game> findByGameStatus(GameStatus gameStatus);

    @Query("SELECT g FROM Game g INNER JOIN g.player1 p1 WHERE p1.userId = g.player1.userId AND g.gameId = :id")
    Game findByGameId(Long id);

    @Query("SELECT COUNT(g) FROM Game g INNER JOIN g.player1 p1 WHERE p1.userId = g.player1.userId " +
            "AND g.player1 = :player AND NOT g.gameStatus = 'ENDED' AND NOT g.gameStatus = 'DELETED'")
    Long countByPlayerOneInGame(Player player);

    @Query("SELECT COUNT(g) FROM Game g INNER JOIN g.player2 p2 WHERE p2.userId = g.player2.userId " +
            "AND g.player2 = :player AND NOT g.gameStatus = 'ENDED' AND NOT g.gameStatus = 'DELETED'")
    Long countByPlayerTwoInGame(Player player);

    @Modifying
    @Transactional
    @Query("UPDATE Game g SET g.player2 = :player where g.gameId = :gameId")
    void updatePlayerTwo(Long gameId, Player player);

    @Modifying
    @Transactional
    @Query("UPDATE Game g SET g.player1 = :player where g.gameId = :gameId")
    void updatePlayerOne(Long gameId, Player player);

    @Modifying
    @Transactional
    @Query("UPDATE Game g SET g.gameStatus = :status where g.gameId = :gameId")
    void updateStatus(Long gameId, GameStatus status);
}
