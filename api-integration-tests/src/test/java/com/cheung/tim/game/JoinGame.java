package com.cheung.tim.game;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.cheung.tim.Config.ENDPOINT;
import static com.cheung.tim.Json.JsonRequest;
import static com.cheung.tim.Json.JsonResponse;
import static com.cheung.tim.Resource.JOIN;
import static com.cheung.tim.game.GetGame.createGame;
import static com.cheung.tim.game.GetGame.createPlayer;
import static io.restassured.RestAssured.given;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class JoinGame {

    private Integer gameId;

    @BeforeEach
    public void setup() {
        this.gameId = createGame(createPlayer());
    }

    @Test
    public void joinGameExisting() {
        String playerId = createPlayer();
        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("joinGame").replacePlayerId(playerId).toString())
                .when()
                .patch(ENDPOINT + JOIN + "/" + gameId.toString());

        assertThat(response.statusCode(), is(204));
        assertThat(response.getBody().asString(), is(""));
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
        String secondGameId = createGame(createPlayer()).toString();
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