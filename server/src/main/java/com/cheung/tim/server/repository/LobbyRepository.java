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
    List<Lobby> findByLobbyStatus(GameStatus gameStatus);

    @Query("SELECT g FROM Lobby g LEFT JOIN g.host p1 WHERE (p1.userId = g.host.userId OR g.host IS NULL) AND g.lobbyId = :id")
    Lobby findByLobbyId(Long id);

    @Query("SELECT p FROM Player p INNER JOIN p.currentLobby g WHERE g.lobbyId = p.currentLobby.lobbyId AND p.userId = :playerId " +
            "AND NOT g.gameStatus = 'ENDED' AND NOT g.gameStatus = 'DELETED'")
    Player getPlayerInLobby(String playerId);

    @Modifying
    @Transactional
    @Query("UPDATE Lobby l SET l.rounds = :rounds WHERE l.lobbyId = :id")
    void updateRounds(Long id, Integer rounds);

    @Modifying
    @Transactional
    @Query("UPDATE Player p SET p.currentLobby = :lobby WHERE p.userId = :id")
    void updatePlayersCurrentLobby(Lobby lobby, String id);

    @Modifying
    @Transactional
    @Query("UPDATE Lobby g SET g.host = :player WHERE g.lobbyId = :lobbyId")
    void updateHost(Long lobbyId, Player player);

    @Modifying
    @Transactional
    @Query("UPDATE Lobby g SET g.gameStatus = :status WHERE g.lobbyId = :lobbyId")
    void updateStatus(Long lobbyId, GameStatus status);
}
