package com.cheung.tim.game;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.cheung.tim.Config.ENDPOINT;
import static com.cheung.tim.Json.JsonRequest;
import static com.cheung.tim.Json.JsonResponse;
import static com.cheung.tim.Resource.*;
import static com.cheung.tim.game.GetGame.createGame;
import static com.cheung.tim.game.GetGame.createPlayer;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class LeaveGame {

    private Integer gameId;
    private String playerOne;
    private String playerTwo;

    @BeforeEach
    public void setup() {
        this.playerOne = createPlayer();
        this.gameId = createGame(this.playerOne);

        this.playerTwo = createPlayer();
        given().contentType(ContentType.JSON)
                .body(JsonRequest("joinGame").replacePlayerId(this.playerTwo).toString())
                .when()
                .patch(ENDPOINT + JOIN + "/" + gameId.toString());
    }

    @Test
    public void leaveGameAsGuest() {
        String expectedGameState = JsonResponse("getGameSuccess")
                .replaceGameId(this.gameId.toString())
                .replaceGameStatus("OPEN")
                .toString();

        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("leaveGame").replacePlayerId(this.playerTwo).toString())
                .when()
                .patch(ENDPOINT + LEAVE + "/" + gameId.toString());

        assertThat(response.statusCode(), is(204));
        assertThat(response.getBody().asString(), is(""));

        Response game = get(ENDPOINT + GAME + "/" + this.gameId);
        assertThat(game.getBody().asString(), jsonEquals(expectedGameState));
    }

    @Test
    public void leaveGameAsHost() {
        String expectedGameState = JsonResponse("getGame")
                .replaceGameId(this.gameId.toString())
                .replaceGameStatus("DELETED")
                .toString();

        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("leaveGame").replacePlayerId(this.playerOne).toString())
                .when()
                .patch(ENDPOINT + LEAVE + "/" + gameId.toString());

        assertThat(response.statusCode(), is(204));
        assertThat(response.getBody().asString(), is(""));

        Response game = get(ENDPOINT + GAME + "/" + this.gameId);
        assertThat(game.getBody().asString(), jsonEquals(expectedGameState));
    }
    @Test
    public void leaveGameAsHostWhenGuestIsPresent() {
        String expectedGameState = JsonResponse("getGame")
                .replaceGameId(this.gameId.toString())
                .replaceGameStatus("DELETED")
                .toString();

        given().contentType(ContentType.JSON)
                .body(JsonRequest("joinGame").replacePlayerId(this.playerTwo).toString())
                .when()
                .patch(ENDPOINT + JOIN + "/" + this.gameId.toString());
        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("leaveGame").replacePlayerId(this.playerOne).toString())
                .when()
                .patch(ENDPOINT + LEAVE + "/" + this.gameId.toString());

        assertThat(response.statusCode(), is(204));
        assertThat(response.getBody().asString(), is(""));

        Response game = get(ENDPOINT + GAME + "/" + this.gameId);
        assertThat(game.getBody().asString(), jsonEquals(expectedGameState));
    }


    @Test
    public void leaveGameThatDoesNotExist() {
        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("leaveGame").replacePlayerId(this.playerOne).toString())
                .when()
                .patch(ENDPOINT + LEAVE + "/0");

        assertThat(response.statusCode(), is(404));
        assertThat(response.getBody().asString(), jsonEquals(JsonResponse("leaveGameNotFound").replaceGameId("0").toString()));
    }

    @Test
    public void leaveOpenGameThatPlayerIsNotIn() {
        String expectedResponse = JsonResponse("leaveGamePlayerNotInThatGame")
                .replaceGameId(this.gameId.toString())
                .replacePlayerName("John")
                .toString();

        String otherPlayer = createPlayer();
        createGame(otherPlayer);

        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("leaveGame").replacePlayerId(otherPlayer).toString())
                .when()
                .patch(ENDPOINT + LEAVE + "/" + this.gameId);

        assertThat(response.statusCode(), is(400));
        assertThat(response.getBody().asString(), jsonEquals(expectedResponse));
    }

    @Test
    public void leaveFullGameThatPlayerIsNotIn() {
        String expectedResponse = JsonResponse("leaveGamePlayerNotInThatGame")
                .replaceGameId(this.gameId.toString())
                .replacePlayerName("John")
                .toString();

        String secondPlayer = createPlayer();
        given().contentType(ContentType.JSON)
                .body(JsonRequest("joinGame").replacePlayerId(secondPlayer).toString())
                .when()
                .patch(ENDPOINT + JOIN + "/" + gameId.toString());

        String otherPlayer = createPlayer();
        createGame(otherPlayer);

        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("leaveGame").replacePlayerId(otherPlayer).toString())
                .when()
                .patch(ENDPOINT + LEAVE + "/" + this.gameId);

        assertThat(response.statusCode(), is(400));
        assertThat(response.getBody().asString(), jsonEquals(expectedResponse));
    }
}
