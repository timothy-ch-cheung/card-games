package com.cheung.tim.server.service;

import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.PlayerDTO;
import com.cheung.tim.server.exception.BadRequestException;
import com.cheung.tim.server.exception.NotFoundException;
import com.cheung.tim.server.repository.PlayerRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerService {

    private PlayerRepository playerRepository;

    public static final String PLAYER_ID_REGEX = "[a-z0-9]{32}";

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player createPlayer(PlayerDTO playerDTO) {
        if (StringUtils.isBlank(playerDTO.getUsername())){
            throw new BadRequestException("Username must not be null or empty");
        }
        Player player = new Player(playerDTO.getUsername());
        return playerRepository.save(player);
    }

    public Player findPlayerById(String userId) throws NotFoundException {
        if (userId == null || !userId.matches(PLAYER_ID_REGEX)){
            throw new BadRequestException("id must match regex [a-z0-9]{32}");
        }
        Optional<Player> player = playerRepository.findById(userId);
        return player.orElseThrow(() -> new NotFoundException(String.format("Player with id %s not found", userId)));
    }
}
