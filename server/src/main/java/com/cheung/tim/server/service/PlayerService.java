package com.cheung.tim.server.service;

import com.cheung.tim.server.domain.Lobby;
import com.cheung.tim.server.domain.Player;
import com.cheung.tim.server.dto.PrivatePlayerDTO;
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

    public Player createPlayer(PrivatePlayerDTO privatePlayerDTO) {
        if (privatePlayerDTO == null || StringUtils.isBlank(privatePlayerDTO.getUsername())) {
            throw new BadRequestException("Username must not be null or empty");
        }
        Player player = new Player(privatePlayerDTO.getUsername());
        return playerRepository.save(player);
    }

    public Player findPlayerById(String userId) {
        if (userId == null || !userId.matches(PLAYER_ID_REGEX)) {
            throw new BadRequestException("id must match regex [a-z0-9]{32}");
        }
        Optional<Player> player = playerRepository.findById(userId);
        return player.orElseThrow(() -> new NotFoundException(String.format("Player with id %s not found", userId)));
    }

    public Player updateCurrentLobby(Player player, Lobby lobby) {
        player.setCurrentLobby(lobby);
        return playerRepository.save(player);
    }

    public Iterable<Player> resetLobby(Iterable<Player> players) {
        players.forEach(p -> p.setCurrentLobby(null));
        return playerRepository.saveAll(players);
    }
}
