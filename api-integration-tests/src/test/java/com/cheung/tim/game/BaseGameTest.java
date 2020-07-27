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
                    .body(JsonRequest("joinGame").replacePlayerId(game.getPlayerId()).toString())
                    .when()
                    .patch(ENDPOINT + LEAVE + "/" + game.getGameId());
        }
    }
}

class Game {
    private String playerId;
    private Integer gameId;

    private Game(String playerId, Integer gameId) {
        this.playerId = playerId;
        this.gameId = gameId;
    }

    public static Game Lobby(String playerId, Integer gameId) {
        return new Game(playerId, gameId);
    }

    public Integer getGameId() {
        return gameId;
    }

    public String getPlayerId() {
        return playerId;
    }
}
