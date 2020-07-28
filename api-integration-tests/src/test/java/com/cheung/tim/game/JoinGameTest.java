package com.cheung.tim.game;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.cheung.tim.Config.ENDPOINT;
import static com.cheung.tim.Json.JsonRequest;
import static com.cheung.tim.Json.JsonResponse;
import static com.cheung.tim.Resource.GAME;
import static com.cheung.tim.Resource.JOIN;
import static com.cheung.tim.game.Game.Lobby;
import static com.cheung.tim.game.GetGameTest.createGame;
import static com.cheung.tim.game.GetGameTest.createPlayer;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static java.lang.Integer.parseInt;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class JoinGameTest extends BaseGameTest {

    private Integer gameId;

    @BeforeEach
    public void setup() {
        String playerId = createPlayer();
        this.gameId = createGame(playerId);
        queueCleanup(Lobby(playerId, gameId));
    }

    @Test
    public void joinGameExisting() {
        String expectedGameState = JsonResponse("getGameReady")
                .replaceGameId(this.gameId.toString())
                .replaceGameStatus("READY")
                .toString();

        String playerId = createPlayer("Jane");
        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("joinGame").replacePlayerId(playerId).toString())
                .when()
                .patch(ENDPOINT + JOIN + "/" + gameId.toString());

        assertThat(response.statusCode(), is(204));
        assertThat(response.getBody().asString(), is(""));

        Response game = get(ENDPOINT + GAME + "/" + this.gameId);
        assertThat(game.getBody().asString(), jsonEquals(expectedGameState));
    }

    @Test
    public void joinGameNonExisting() {
        String playerId = createPlayer();
        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("joinGame").replacePlayerId(playerId).toString())
                .when()
                .patch(ENDPOINT + JOIN + "/0");

        assertThat(response.statusCode(), is(404));
        assertThat(response.getBody().asString(), jsonEquals(JsonResponse("joinGameNotFound").replaceGameId("0").toString()));
    }

    @Test
    public void joinGamePlayerAlreadyInGame() {
        String secondPlayerId = createPlayer();
        String secondGameId = createGame(secondPlayerId).toString();
        String playerId = createPlayer();
        given().contentType(ContentType.JSON)
                .body(JsonRequest("joinGame").replacePlayerId(playerId).toString())
                .when()
                .patch(ENDPOINT + JOIN + "/" + gameId.toString());

        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("joinGame").replacePlayerId(playerId).toString())
                .when()
                .patch(ENDPOINT + JOIN + "/" + secondGameId);

        assertThat(response.statusCode(), is(400));
        assertThat(response.getBody().asString(), jsonEquals(JsonResponse("joinGameAlreadyInGame").replaceGameId(secondGameId).toString()));

        queueCleanup(Lobby(secondPlayerId, parseInt(secondGameId)));
    }

    @Test
    public void joinGameAlreadyFull() {
        String expectedResponse = JsonResponse("joinGameAlreadyFull").replaceGameId(this.gameId.toString()).toString();
        String playerOneId = createPlayer();
        given().contentType(ContentType.JSON)
                .body(JsonRequest("joinGame").replacePlayerId(playerOneId).toString())
                .when()
                .patch(ENDPOINT + JOIN + "/" + this.gameId.toString());

        String playerTwoId = createPlayer();
        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("joinGame").replacePlayerId(playerTwoId).toString())
                .when()
                .patch(ENDPOINT + JOIN + "/" + this.gameId.toString());

        assertThat(response.statusCode(), is(400));
        assertThat(response.getBody().asString(), jsonEquals(expectedResponse));
    }
}
