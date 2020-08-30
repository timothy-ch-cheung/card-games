package com.cheung.tim.server.repository;

import com.cheung.tim.server.domain.Lobby;
import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.enums.GameStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LobbyRepository extends CrudRepository<Lobby, Long> {

    @Query("SELECT g FROM Lobby g INNER JOIN g.host p1 WHERE p1.userId = g.host.userId AND g.gameStatus = :gameStatus")
    List<Lobby> findByLobbyStatus(GameStatus gameStatus);

    @Query("SELECT g FROM Lobby g LEFT JOIN g.host p1 WHERE (p1.userId = g.host.userId OR g.host IS NULL) AND g.lobbyId = :id")
    Lobby findByLobbyId(Long id);

    @Query("SELECT p FROM Player p INNER JOIN p.currentLobby g WHERE g.lobbyId = p.currentLobby.lobbyId AND p.userId = :playerId " +
            "AND NOT g.gameStatus = 'ENDED' AND NOT g.gameStatus = 'DELETED'")
    Player getPlayerInLobby(String playerId);
}
