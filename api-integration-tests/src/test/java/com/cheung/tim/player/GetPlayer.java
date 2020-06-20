package com.cheung.tim.player;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.cheung.tim.Config.ENDPOINT;
import static com.cheung.tim.Json.JsonResponse;
import static com.cheung.tim.Resource.PLAYER;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class GetPlayer {

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

        String expectedBody = JsonResponse("getPlayerSuccess").replacePlayerId(playerId).toString();
        String actualBody = response.getBody().asString();

        assertThat(response.statusCode(), is(200));
        assertThat(actualBody, jsonEquals(expectedBody));
    }

    @Test
    public void getNonExistingPlayer() {
        Response response = get(ENDPOINT + PLAYER + "/abababababababababababababababab");

        String expectedBody = JsonResponse("getPlayerNotFound").replacePlayerId("abababababababababababababababab").toString();
        String actualBody = response.getBody().asString();

        assertThat(response.statusCode(), is(404));
        assertThat(actualBody, is(jsonEquals(expectedBody)));
    }

    @Test
    public void getPlayerIncorrectIdFormat() {
        Response response = get(ENDPOINT + PLAYER + "/abcde");

        String expectedBody = JsonResponse("getPlayerIncorrectFormat").replacePlayerId("abcde").toString();
        String actualBody = response.getBody().asString();

        assertThat(response.statusCode(), is(400));
        assertThat(actualBody, is(jsonEquals(expectedBody)));
    }
}
