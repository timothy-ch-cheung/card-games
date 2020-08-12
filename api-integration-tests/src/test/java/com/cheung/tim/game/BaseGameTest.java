package com.cheung.tim.game;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;

import java.util.ArrayList;

import static com.cheung.tim.Config.ENDPOINT;
import static com.cheung.tim.Json.JsonRequest;
import static com.cheung.tim.Resource.LEAVE;
import static io.restassured.RestAssured.given;

public abstract class BaseGameTest {
    public ArrayList<Game> gamesToCleanup = new ArrayList<>();

    public void queueCleanup(Game game) {
        gamesToCleanup.add(game);
    }

    @AfterEach
    public void cleanup() {
        for (Game game : gamesToCleanup) {
            given().contentType(ContentType.JSON)
                    .body(JsonRequest("joinGame").replacePlayerId(game.getPlayerOneId()).toString())
                    .when()
                    .patch(ENDPOINT + LEAVE + "/" + game.getGameId());
        }
    }
}

class Game {
    private Player player;
    private Integer gameId;

    private Game(Player player, Integer gameId) {
        this.player = player;
        this.gameId = gameId;
    }

    public static Game game(Player player, Integer gameId) {
        return new Game(player, gameId);
    }

    public Integer getGameId() {
        return gameId;
    }

    public String getPlayerOneId() {
        return player.getId();
    }

    public String getPlayerOneKey() {
        return player.getKey();
    }
}

class Player {
    String userId;
    String key;

    private Player(String userId, String key) {
        this.userId = userId;
        this.key = key;
    }

    public static Player player(String userId, String key) {
        return new Player(userId, key);
    }

    public String getId() {
        return this.userId;
    }

    public String getKey() {
        return this.key;
    }
}
