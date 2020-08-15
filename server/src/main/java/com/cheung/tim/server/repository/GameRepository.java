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

    @Query("SELECT g FROM Game g INNER JOIN g.host p1 WHERE p1.userId = g.host.userId AND g.gameStatus = :gameStatus")
    List<Game> findByGameStatus(GameStatus gameStatus);

    @Query("SELECT g FROM Game g LEFT JOIN g.host p1 WHERE (p1.userId = g.host.userId OR g.host IS NULL) AND g.gameId = :id")
    Game findByGameId(Long id);

    @Query("SELECT p FROM Player p INNER JOIN p.currentGame g WHERE g.gameId = p.currentGame.gameId AND p.userId = :playerId " +
            "AND NOT g.gameStatus = 'ENDED' AND NOT g.gameStatus = 'DELETED'")
    Player getPlayerInGame(String playerId);

    @Modifying
    @Transactional
    @Query("Update Player p SET p.currentGame = :game where p.userId = :id")
    void updatePlayersCurrentGame(Game game, String id);

    @Modifying
    @Transactional
    @Query("UPDATE Game g SET g.host = :player where g.gameId = :gameId")
    void updateHost(Long gameId, Player player);

    @Modifying
    @Transactional
    @Query("UPDATE Game g SET g.gameStatus = :status where g.gameId = :gameId")
    void updateStatus(Long gameId, GameStatus status);
}
