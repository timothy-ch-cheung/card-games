package com.cheung.tim.server.repository;

import com.cheung.tim.server.domain.Lobby;
import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.enums.GameStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LobbyRepository extends CrudRepository<Lobby, Long> {

    @Query("SELECT g FROM Lobby g INNER JOIN g.host p1 WHERE p1.userId = g.host.userId AND g.gameStatus = :gameStatus")
    List<Lobby> findByGameStatus(GameStatus gameStatus);

    @Query("SELECT g FROM Lobby g LEFT JOIN g.host p1 WHERE (p1.userId = g.host.userId OR g.host IS NULL) AND g.gameId = :id")
    Lobby findByGameId(Long id);

    @Query("SELECT p FROM Player p INNER JOIN p.currentLobby g WHERE g.gameId = p.currentLobby.gameId AND p.userId = :playerId " +
            "AND NOT g.gameStatus = 'ENDED' AND NOT g.gameStatus = 'DELETED'")
    Player getPlayerInGame(String playerId);

    @Modifying
    @Transactional
    @Query("UPDATE Player p SET p.currentLobby = :lobby WHERE p.userId = :id")
    void updatePlayersCurrentGame(Lobby lobby, String id);

    @Modifying
    @Transactional
    @Query("UPDATE Lobby g SET g.host = :player WHERE g.gameId = :gameId")
    void updateHost(Long gameId, Player player);

    @Modifying
    @Transactional
    @Query("UPDATE Lobby g SET g.gameStatus = :status WHERE g.gameId = :gameId")
    void updateStatus(Long gameId, GameStatus status);
}
