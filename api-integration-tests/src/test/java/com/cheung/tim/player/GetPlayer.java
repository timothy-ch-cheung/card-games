package com.cheung.tim.player;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.cheung.tim.Config.ENDPOINT;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

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
    public void createPlayer() {
        Response response = get(ENDPOINT + PLAYER + "/" + playerId);
        response.then().body("username", is("John"));
    }
}
