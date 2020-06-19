package com.cheung.tim.player;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static com.cheung.tim.Config.ENDPOINT;
import static com.cheung.tim.Json.JsonRequest;
import static com.cheung.tim.Json.JsonResponse;
import static com.cheung.tim.Resource.PLAYER;
import static io.restassured.RestAssured.given;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreatePlayer {

    @Test
    public void createPlayerValidUsername() {
        Response response = given().contentType(ContentType.JSON)
                .body(JsonRequest("createPlayerValid").toString())
                .when()
                .post(ENDPOINT + PLAYER);

        assertThat(response.statusCode(), is(200));
        assertThat(response.getBody().asString(), jsonEquals(JsonResponse("createPlayerSuccess").toString()));
    }

}
