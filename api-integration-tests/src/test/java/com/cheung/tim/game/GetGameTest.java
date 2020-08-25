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
import static com.cheung.tim.game.Game.game;
import static com.cheung.tim.game.GetGameTest.createGame;
import static com.cheung.tim.game.GetGameTest.createPlayer;
import static com.cheung.tim.game.Player.player;
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
        Player player = createPlayer();
        this.gameId = createGame(player.getId(), player.getKey());
        queueCleanup(game(player, gameId));
    }

    @Test
    public void getGameWithValidId() {
        String expectedResponse = JsonResponse("getGameSuccess")
                .replaceGameId(this.gameId.toString())
                .replaceGameStatus("OPEN")
                .replaceRounds("2")
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

    public static Player createPlayer() {
        return createPlayer("John");
    }

    public static Player createPlayer(String name) {
        Response response = given().contentType(ContentType.JSON)
                .body(String.format("{\"username\": \"%s\"}", name))
                .when()
                .post(ENDPOINT + PLAYER);
        return player(response.path("id"), response.path("key"));
    }

    public static Integer createGame(String playerId, String key) {
        String request = JsonRequest("createGame")
                .replaceLobbyName("test lobby")
                .replacePlayerId(playerId)
                .replaceKey(key)
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
        ArrayList<Player> players = new ArrayList();
        ArrayList<Integer> games = new ArrayList();
        for (int i = 0; i < 3; i++) {
            players.add(createPlayer());
        }
        for (Player p : players) {
            games.add(createGame(p.getId(), p.getKey()));
        }

        for (int i = 0; i < games.size(); i++) {
            queueCleanup(game(players.get(i), games.get(i)));
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
