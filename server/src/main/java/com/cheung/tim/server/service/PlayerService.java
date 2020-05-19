package com.cheung.tim.server.service;

import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.PlayerDTO;
import com.cheung.tim.server.exception.NotFoundException;
import com.cheung.tim.server.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerService {

    private PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player createPlayer(PlayerDTO playerDTO) {
        Player player = new Player(playerDTO.getUsername());
        return playerRepository.save(player);
    }

    public Player findPlayerById(String userId) throws NotFoundException {
        Optional<Player> player = playerRepository.findById(userId);
        return player.orElseThrow(() -> new NotFoundException(String.format("Player with id %s not found", userId)));
    }
}
