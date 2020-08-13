package com.cheung.tim.game;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.cheung.tim.Config.ENDPOINT;
import static com.cheung.tim.Json.JsonRequest;
import static com.cheung.tim.Json.JsonResponse;
import static com.cheung.tim.Resource.CREATE;
import static com.cheung.tim.Resource.PLAYER;
import static com.cheung.tim.game.Game.game;
import static com.cheung.tim.game.Player.player;
import static io.restassured.RestAssured.given;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreateGameTest extends BaseGameTest {

    private Player player;

    @BeforeEach
    public void setup() {
        Response response = given().contentType(ContentType.JSON)
                .body("{\"username\": \"John\"}")
                .when()
                .post(ENDPOINT + PLAYER);
        this.player = player(response.path("id"),response.path("key"));
    }

    @Test
    public void createGameExistingPlayer() {
        String request = JsonRequest("createGame")
                .replaceLobbyName("test lobby")
                .replacePlayerId(player.getId())
                .replaceKey(player.getKey())
                .toString();

        Response response = given().contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(ENDPOINT + CREATE);

        String expectedResponse = JsonResponse("createGameSuccess")
                .replacePlayerId(player.getId())
                .replaceKey(player.getKey())
                .toString();

        assertThat(response.statusCode(), is(200));
        assertThat(response.getBody().asString(), jsonEquals(expectedResponse));

        queueCleanup(game(player, response.path("id")));
    }

    @Test
    public void createGameNonExistingPlayer() {
        String request = JsonRequest("createGame")
                .replaceLobbyName("test lobby")
                .replacePlayerId("abababababababababababababababab")
                .toString();

        Response response = given().contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(ENDPOINT + CREATE);

        String expectedResponse = JsonResponse("createGamePlayerNotFound")
                .replacePlayerId("abababababababababababababababab")
                .toString();

        assertThat(response.statusCode(), is(404));
        assertThat(response.getBody().asString(), jsonEquals(expectedResponse));
    }

    @Test
    public void createGamePlayerWithInvalidKey() {
        String request = JsonRequest("createGame")
                .replaceLobbyName("test lobby")
                .replacePlayerId(player.getId())
                .replaceKey("invalidinvalidinvalidinvalidinva")
                .toString();

        Response response = given().contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(ENDPOINT + CREATE);

        String expectedResponse = JsonResponse("createGameIncorrectFormat")
                .replacePlayerId("abababababababababababababababab")
                .replaceMessage("Player id or key invalid")
                .toString();

        assertThat(response.statusCode(), is(400));
        assertThat(response.getBody().asString(), jsonEquals(expectedResponse));
    }

    @Test
    public void createGameEmptyBody() {
        Response response = given().contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post(ENDPOINT + CREATE);

        String expectedResponse = JsonResponse("createGameIncorrectFormat")
                .replacePlayerId("abababababababababababababababab")
                .replaceMessage("Lobby name or Host not supplied")
                .toString();

        assertThat(response.statusCode(), is(400));
        assertThat(response.getBody().asString(), jsonEquals(expectedResponse));
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "   "})
    public void createGameEmptyLobbyName(String lobbyName) {
        String request = JsonRequest("createGame")
                .replaceLobbyName(lobbyName)
                .replacePlayerId(player.getId())
                .replaceKey(player.getKey())
                .toString();

        Response response = given().contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(ENDPOINT + CREATE);

        String expectedResponse = JsonResponse("createGameIncorrectFormat")
                .replacePlayerId(player.getId())
                .replaceKey(player.getKey())
                .replaceMessage("Lobby name or Host not supplied")
                .toString();

        assertThat(response.statusCode(), is(400));
        assertThat(response.getBody().asString(), jsonEquals(expectedResponse));
    }
}
