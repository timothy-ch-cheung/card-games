package com.cheung.tim.game;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;

import static com.cheung.tim.Config.ENDPOINT;
import static com.cheung.tim.Json.JsonRequest;
import static com.cheung.tim.Resource.*;
import static com.cheung.tim.game.GetGame.createGame;
import static com.cheung.tim.game.GetGame.createPlayer;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class GetGame {
    @BeforeEach
    public void setup() {

    }

    @Test
    public void getGameWithValidId() {

    }

    public static String createPlayer() {
        Response response = given().contentType(ContentType.JSON)
                .body("{\"username\": \"John\"}")
                .when()
                .post(ENDPOINT + PLAYER);
        return response.path("id");
    }

    public static Integer createGame(String playerId) {
        String request = JsonRequest("createGame")
                .replaceLobbyName("test lobby")
                .replacePlayerId(playerId)
                .toString();

        Response response = given().contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(ENDPOINT + CREATE);
        return response.path("id");
    }
}

class GetGames {
    @BeforeEach
    public void setup() {
        ArrayList<String> players = new ArrayList();
        for (int i = 0; i < 3; i++) {
            players.add(createPlayer());
        }
        for (String id : players) {
            createGame(id);
        }
    }

    @Test
    public void getGamesShouldReturnAllOpenGames() throws Exception {
        Response response = get(ENDPOINT + GAMES);

        ArrayList<Map<String, String>> games = response.path("games");
        assertThat(response.path("numOpenGames"), is(games.size()));
    }
}
