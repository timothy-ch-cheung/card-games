package com.cheung.tim.game;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.cheung.tim.Config.ENDPOINT;
import static com.cheung.tim.Json.JsonRequest;
import static com.cheung.tim.Json.JsonResponse;
import static com.cheung.tim.Resource.JOIN;
import static com.cheung.tim.Resource.UPDATE;
import static com.cheung.tim.game.Game.game;
import static com.cheung.tim.game.GetGameTest.createGame;
import static com.cheung.tim.game.GetGameTest.createPlayer;
import static io.restassured.RestAssured.given;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UpdateGameTest extends BaseGameTest {

    private Integer gameId;
    private Player host;


    @BeforeEach
    public void setup() {
        this.host = createPlayer();
        this.gameId = createGame(host.getId(), host.getKey());
        queueCleanup(game(host, gameId));
    }

    @Test
    void updateRoundsAsHost() {
        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("updateGame").replacePlayerId(this.host.getId())
                        .replaceKey(this.host.getKey())
                        .replaceRounds("4").toString())
                .when()
                .patch(ENDPOINT + UPDATE + "/" + gameId.toString());

        assertThat(response.statusCode(), is(204));
        assertThat(response.getBody().asString(), is(""));
    }

    @Test
    void updateRoundsAsHostIncorrectKey() {
        String expectedResponse = JsonResponse("updateGameBadRequest")
                .replaceGameId(this.gameId.toString())
                .replaceMessage("Player id or key invalid").toString();

        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("updateGame").replacePlayerId(this.host.getId())
                        .replaceKey("invalidinvalidinvalidinvalidinva")
                        .replaceRounds("4").toString())
                .when()
                .patch(ENDPOINT + UPDATE + "/" + gameId.toString());

        assertThat(response.statusCode(), is(400));
        assertThat(response.getBody().asString(), jsonEquals(expectedResponse));
    }

    @Test
    void updateRoundsAsGuest() {
        String expectedResponse = JsonResponse("updateGameBadRequest")
                .replaceGameId(this.gameId.toString())
                .replaceMessage("Only host can update lobby").toString();

        Player guest = createPlayer();
        given().contentType(ContentType.JSON)
                .body(JsonRequest("joinGame").replacePlayerId(guest.getId())
                        .replaceKey(guest.getKey()).toString())
                .when()
                .patch(ENDPOINT + JOIN + "/" + this.gameId);

        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("updateGame").replacePlayerId(guest.getId())
                        .replaceKey(guest.getKey())
                        .replaceRounds("4").toString())
                .when()
                .patch(ENDPOINT + UPDATE + "/" + gameId.toString());

        assertThat(response.statusCode(), is(400));
        assertThat(response.getBody().asString(), jsonEquals(expectedResponse));
    }
}
