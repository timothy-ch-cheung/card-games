package com.cheung.tim.server.repository;

import com.cheung.tim.server.domain.Player;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PlayerRepository extends CrudRepository<Player, Long> {

    @Query("SELECT p FROM Player p WHERE p.userId = :id")
    Optional<Player> findById(String id);
}
