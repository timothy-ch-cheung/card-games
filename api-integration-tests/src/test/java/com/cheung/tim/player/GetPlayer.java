package com.cheung.tim.player;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.cheung.tim.Config.ENDPOINT;
import static com.cheung.tim.Util.loadResponse;
import static com.cheung.tim.Util.maskId;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class GetPlayer {

    private static final String PLAYER = "/player";

    private String playerId;

    @BeforeEach
    public void setup() {
        Response response = given().contentType(ContentType.JSON)
                .body("{\"username\": \"John\"}")
                .when()
                .post(ENDPOINT + PLAYER);
        this.playerId = response.path("id");
    }

    @Test
    public void getExistingPlayer() {
        Response response = get(ENDPOINT + PLAYER + "/" + playerId);
        String actualBody = maskId(response.getBody().asString());

        assertThat(response.statusCode(), is(200));
        assertThat(actualBody, is(jsonEquals(loadResponse("getPlayerSuccess"))));
    }
}
