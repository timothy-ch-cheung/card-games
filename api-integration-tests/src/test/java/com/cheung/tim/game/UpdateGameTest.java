package com.cheung.tim.game;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.cheung.tim.Config.ENDPOINT;
import static com.cheung.tim.Json.JsonRequest;
import static com.cheung.tim.Resource.UPDATE;
import static com.cheung.tim.game.Game.game;
import static com.cheung.tim.game.GetGameTest.createGame;
import static com.cheung.tim.game.GetGameTest.createPlayer;
import static io.restassured.RestAssured.given;
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
}
