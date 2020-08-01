package com.cheung.tim.game;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;

import static com.cheung.tim.Config.ENDPOINT;
import static com.cheung.tim.Json.JsonRequest;
import static com.cheung.tim.Json.JsonResponse;
import static com.cheung.tim.Resource.*;
import static com.cheung.tim.game.Game.Lobby;
import static com.cheung.tim.game.GetGameTest.createGame;
import static com.cheung.tim.game.GetGameTest.createPlayer;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GetGameTest extends BaseGameTest {
    private Integer gameId;

    @BeforeEach
    public void setup() {
        String playerId = createPlayer();
        this.gameId = createGame(playerId);
        queueCleanup(Lobby(playerId, gameId));
    }

    @Test
    public void getGameWithValidId() {
        String expectedResponse = JsonResponse("getGameSuccess")
                .replaceGameId(this.gameId.toString())
                .replaceGameStatus("OPEN")
                .toString();
        Response response = get(ENDPOINT + GAME + "/" + this.gameId);

        assertThat(response.getStatusCode(), is(200));
        assertThat(response.getBody().asString(), jsonEquals(expectedResponse));
    }

    @Test
    public void getGameWithNonExistingId() {
        String expectedResponse = JsonResponse("getGameNotFound").replaceGameId("0").toString();
        Response response = get(ENDPOINT + GAME + "/0");

        assertThat(response.getStatusCode(), is(404));
        assertThat(response.getBody().asString(), jsonEquals(expectedResponse));
    }

    public static String createPlayer() {
        return createPlayer("John");
    }

    public static String createPlayer(String name) {
        Response response = given().contentType(ContentType.JSON)
                .body(String.format("{\"username\": \"%s\"}", name))
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

class GetGamesTest extends BaseGameTest {
    @BeforeEach
    public void setup() {
        ArrayList<String> players = new ArrayList();
        ArrayList<Integer> games = new ArrayList();
        for (int i = 0; i < 3; i++) {
            players.add(createPlayer());
        }
        for (String id : players) {
            games.add(createGame(id));
        }

        for (int i = 0; i < games.size(); i++) {
            queueCleanup(Lobby(players.get(i), games.get(i)));
        }
    }

    @Test
    public void getGamesShouldReturnAllOpenGames() throws Exception {
        Response response = get(ENDPOINT + GAMES);

        ArrayList<Map<String, Object>> games = response.path("games");
        assertThat(response.path("numOpenGames"), is(games.size()));

        for (Map<String, Object> game : games) {
            Response gameResponse = get(ENDPOINT + GAME + "/" + game.get("id"));
            for (String key : game.keySet()) {
                assertEquals(game.get(key), gameResponse.path(key));
            }
        }
    }
}