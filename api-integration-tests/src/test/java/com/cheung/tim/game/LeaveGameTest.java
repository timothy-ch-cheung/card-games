package com.cheung.tim.game;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.cheung.tim.Config.ENDPOINT;
import static com.cheung.tim.Json.JsonRequest;
import static com.cheung.tim.Json.JsonResponse;
import static com.cheung.tim.Resource.*;
import static com.cheung.tim.game.Game.game;
import static com.cheung.tim.game.GetGameTest.createGame;
import static com.cheung.tim.game.GetGameTest.createPlayer;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class LeaveGameTest extends BaseGameTest {

    private Integer gameId;
    private Player playerOne;
    private Player playerTwo;

    @BeforeEach
    public void setup() {
        this.playerOne = createPlayer();
        this.gameId = createGame(playerOne.getId(), playerOne.getKey());

        this.playerTwo = createPlayer();
        given().contentType(ContentType.JSON)
                .body(JsonRequest("joinGame").replacePlayerId(playerTwo.getId()).replaceKey(playerTwo.getKey()).toString())
                .when()
                .patch(ENDPOINT + JOIN + "/" + gameId.toString());

        queueCleanup(game(playerOne, gameId));
    }

    @Test
    public void leaveGameAsGuest() {
        String expectedGameState = JsonResponse("getGameSuccess")
                .replaceGameId(this.gameId.toString())
                .replaceGameStatus("OPEN")
                .replaceRounds("2")
                .toString();

        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("leaveGame").replacePlayerId(this.playerTwo.getId())
                        .replaceKey(this.playerTwo.getKey()).toString())
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
                .replaceRounds("2")
                .toString();

        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("leaveGame").replacePlayerId(this.playerOne.getId())
                        .replaceKey(this.playerOne.getKey()).toString())
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
                .replaceRounds("2")
                .toString();

        given().contentType(ContentType.JSON)
                .body(JsonRequest("joinGame").replacePlayerId(this.playerTwo.getId())
                        .replaceKey(this.playerTwo.getKey()).toString())
                .when()
                .patch(ENDPOINT + JOIN + "/" + this.gameId.toString());
        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("leaveGame").replacePlayerId(this.playerOne.getId())
                        .replaceKey(this.playerOne.getKey()).toString())
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
                .body(JsonRequest("leaveGame").replacePlayerId(this.playerOne.getId())
                        .replaceKey(this.playerOne.getKey()).toString())
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

        Player otherPlayer = createPlayer();
        Integer otherGame = createGame(otherPlayer.getId(), otherPlayer.getKey());

        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("leaveGame").replacePlayerId(otherPlayer.getId()).replaceKey(otherPlayer.getKey()).toString())
                .when()
                .patch(ENDPOINT + LEAVE + "/" + this.gameId);

        assertThat(response.statusCode(), is(400));
        assertThat(response.getBody().asString(), jsonEquals(expectedResponse));

        queueCleanup(game(otherPlayer, otherGame));
    }

    @Test
    public void leaveFullGameThatPlayerIsNotIn() {
        String expectedResponse = JsonResponse("leaveGamePlayerNotInThatGame")
                .replaceGameId(this.gameId.toString())
                .replacePlayerName("John")
                .toString();

        Player secondPlayer = createPlayer();
        given().contentType(ContentType.JSON)
                .body(JsonRequest("joinGame").replacePlayerId(secondPlayer.getId())
                        .replaceKey(secondPlayer.getKey()).toString())
                .when()
                .patch(ENDPOINT + JOIN + "/" + gameId.toString());

        Player otherPlayer = createPlayer();
        Integer otherGame = createGame(otherPlayer.getId(), otherPlayer.getKey());

        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("leaveGame").replacePlayerId(otherPlayer.getId())
                        .replaceKey(otherPlayer.getKey()).toString())
                .when()
                .patch(ENDPOINT + LEAVE + "/" + this.gameId);

        assertThat(response.statusCode(), is(400));
        assertThat(response.getBody().asString(), jsonEquals(expectedResponse));

        queueCleanup(game(otherPlayer, otherGame));
    }

    @Test
    public void leaveGameAsGuestInvalidKey() {
        String expectedResponse = JsonResponse("leaveGameBadRequest")
                .replaceGameId(this.gameId.toString())
                .replaceMessage("Player id or key invalid")
                .toString();

        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("leaveGame").replacePlayerId(this.playerTwo.getId())
                        .replaceKey("invalidinvalidinvalidinvalidinva").toString())
                .when()
                .patch(ENDPOINT + LEAVE + "/" + gameId.toString());

        assertThat(response.statusCode(), is(400));
        assertThat(response.getBody().asString(), jsonEquals(expectedResponse));
    }

    @Test
    public void leaveGameAsHostInvalidKey() {
        String expectedResponse = JsonResponse("leaveGameBadRequest")
                .replaceGameId(this.gameId.toString())
                .replaceMessage("Player id or key invalid")
                .toString();

        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("leaveGame").replacePlayerId(this.playerOne.getId())
                        .replaceKey("invalidinvalidinvalidinvalidinva").toString())
                .when()
                .patch(ENDPOINT + LEAVE + "/" + gameId.toString());

        assertThat(response.statusCode(), is(400));
        assertThat(response.getBody().asString(), jsonEquals(expectedResponse));
    }
}
