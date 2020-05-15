package com.cheung.tim.server.service;

import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.PlayerDTO;
import com.cheung.tim.server.exception.NotFoundException;
import com.cheung.tim.server.repository.PlayerRepository;
import org.springframework.stereotype.Service;

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

    public Player findPlayerById(PlayerDTO playerDTO) throws NotFoundException {
        Player player = playerRepository.findById(playerDTO.getId()).get();
        if (player == null) {
            throw new NotFoundException(String.format("Player with id %s not found", playerDTO.getId()));
        }
        return player;
    }
}
