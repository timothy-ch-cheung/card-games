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
import static com.cheung.tim.game.Game.game;
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
        Player player = createPlayer();
        this.gameId = createGame(player.getId(), player.getKey());
        queueCleanup(game(player, gameId));
    }

    @Test
    public void joinGameExisting() {
        String expectedGameState = JsonResponse("getGameReady")
                .replaceGameId(this.gameId.toString())
                .replaceGameStatus("READY")
                .toString();

        Player player = createPlayer("Jane");
        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("joinGame").replacePlayerId(player.getId())
                        .replaceKey(player.getKey()).toString())
                .when()
                .patch(ENDPOINT + JOIN + "/" + gameId.toString());

        assertThat(response.statusCode(), is(204));
        assertThat(response.getBody().asString(), is(""));

        Response game = get(ENDPOINT + GAME + "/" + this.gameId);
        assertThat(game.getBody().asString(), jsonEquals(expectedGameState));
    }

    @Test
    public void joinGameNonExisting() {
        Player player = createPlayer();
        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("joinGame").replacePlayerId(player.getId())
                        .replaceKey(player.getKey()).toString())
                .when()
                .patch(ENDPOINT + JOIN + "/0");

        assertThat(response.statusCode(), is(404));
        assertThat(response.getBody().asString(), jsonEquals(JsonResponse("joinGameNotFound").replaceGameId("0").toString()));
    }

    @Test
    public void joinGamePlayerAlreadyInGame() {
        Player secondPlayer = createPlayer();
        String secondGameId = createGame(secondPlayer.getId(), secondPlayer.getKey()).toString();
        Player player = createPlayer();
        given().contentType(ContentType.JSON)
                .body(JsonRequest("joinGame").replacePlayerId(player.getId())
                        .replaceKey(player.getKey()).toString())
                .when()
                .patch(ENDPOINT + JOIN + "/" + gameId.toString());

        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("joinGame").replacePlayerId(player.getId())
                        .replaceKey(player.getKey()).toString())
                .when()
                .patch(ENDPOINT + JOIN + "/" + secondGameId);

        assertThat(response.statusCode(), is(400));
        assertThat(response.getBody().asString(), jsonEquals(JsonResponse("joinGameBadRequest")
                .replaceMessage("Player John is already in a game").replaceGameId(secondGameId).toString()));

        queueCleanup(game(secondPlayer, parseInt(secondGameId)));
    }

    @Test
    public void joinGameAlreadyFull() {
        String expectedResponse = JsonResponse("joinGameAlreadyFull").replaceGameId(this.gameId.toString()).toString();
        Player playerOne = createPlayer();
        given().contentType(ContentType.JSON)
                .body(JsonRequest("joinGame").replacePlayerId(playerOne.getId())
                        .replaceKey(playerOne.getKey()).toString())
                .when()
                .patch(ENDPOINT + JOIN + "/" + this.gameId.toString());

        Player playerTwo = createPlayer();
        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("joinGame").replacePlayerId(playerTwo.getId())
                        .replaceKey(playerTwo.getKey()).toString())
                .when()
                .patch(ENDPOINT + JOIN + "/" + this.gameId.toString());

        assertThat(response.statusCode(), is(400));
        assertThat(response.getBody().asString(), jsonEquals(expectedResponse));
    }

    @Test
    public void joinGameInvalidPlayerKey() {
        String expectedResponse = JsonResponse("joinGameBadRequest").replaceGameId(this.gameId.toString())
                .replaceMessage("Player id or key invalid").toString();
        Player playerOne = createPlayer();
        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("joinGame").replacePlayerId(playerOne.getId())
                        .replaceKey("invalidinvalidinvalidinvalidinva").toString())
                .when()
                .patch(ENDPOINT + JOIN + "/" + this.gameId.toString());

        assertThat(response.statusCode(), is(400));
        assertThat(response.getBody().asString(), jsonEquals(expectedResponse));
    }
}
