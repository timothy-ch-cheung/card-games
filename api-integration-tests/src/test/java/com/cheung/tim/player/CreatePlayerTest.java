package com.cheung.tim.player;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.cheung.tim.Config.ENDPOINT;
import static com.cheung.tim.Json.JsonRequest;
import static com.cheung.tim.Json.JsonResponse;
import static com.cheung.tim.Resource.PLAYER;
import static io.restassured.RestAssured.given;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;

public class CreatePlayerTest {

    public static final String PLAYER_ID_REGEX = "[a-z0-9]{32}";

    @Test
    public void createPlayerValidUsername() {
        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("createPlayerValid").toString())
                .when()
                .post(ENDPOINT + PLAYER);

        assertThat(response.statusCode(), is(200));
        assertThat(response.getBody().path("id"), matchesPattern(PLAYER_ID_REGEX));
        assertThat(response.getBody().asString(), jsonEquals(JsonResponse("createPlayerSuccess").toString()));
    }

    @Test
    public void createPlayerEmptyBody() {
        Response response = given().contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post(ENDPOINT + PLAYER);

        assertThat(response.statusCode(), is(400));
        assertThat(response.getBody().asString(), jsonEquals(JsonResponse("createPlayerEmptyBody").toString()));
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "   "})
    public void createPlayerEmptyPlayerName(String playerName) {
        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("createPlayer").replacePlayerName(playerName).toString())
                .when()
                .post(ENDPOINT + PLAYER);

        assertThat(response.statusCode(), is(400));
        assertThat(response.getBody().asString(), jsonEquals(JsonResponse("createPlayerEmptyBody").toString()));
    }

}
