package com.cheung.tim;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Json {
    private static final String BASE_DIR = "src/test/resources/";

    private static final String PLAYER_ID_TOKEN = "${player_id}";
    private static final String GAME_ID_TOKEN = "${game_id}";
    private static final String PLAYER_NAME_TOKEN = "${player_name}";
    private static final String LOBBY_NAME_TOKEN = "${lobby_name}";
    private static final String GAME_STATUS_TOKEN = "${game_status}";
    private static final String KEY_TOKEN = "${key}";
    private static final String MESSAGE_TOKEN = "${message}";
    private static final String GUESTS_TOKEN = "${guests}";
    private static final String ROUNDS_TOKEN = "${rounds}";

    private String json;

    private Json(String json) {
        this.json = json;
    }

    public static Json JsonResponse(String responseName) {
        return new Json(loadJson(BASE_DIR + "responses/", responseName));
    }

    public static Json JsonRequest(String requestName) {
        return new Json(loadJson(BASE_DIR + "requests/", requestName));
    }

    public Json replacePlayerId(String id) {
        this.json = this.json.replace(PLAYER_ID_TOKEN, id);
        return this;
    }

    public Json replacePlayerName(String name) {
        this.json = this.json.replace(PLAYER_NAME_TOKEN, name);
        return this;
    }

    public Json replaceGameId(String id) {
        this.json = this.json.replace(GAME_ID_TOKEN, id);
        return this;
    }

    public Json replaceLobbyName(String name) {
        this.json = this.json.replace(LOBBY_NAME_TOKEN, name);
        return this;
    }

    public Json replaceGameStatus(String name) {
        this.json = this.json.replace(GAME_STATUS_TOKEN, name);
        return this;
    }

    public Json replaceKey(String key) {
        this.json = this.json.replace(KEY_TOKEN, key);
        return this;
    }

    public Json replaceMessage(String message) {
        this.json = this.json.replace(MESSAGE_TOKEN, message);
        return this;
    }

    public Json replaceGuests(String guests) {
        this.json = this.json.replace(GUESTS_TOKEN, guests);
        return this;
    }

    public Json replaceRounds(String rounds) {
        this.json = this.json.replace(ROUNDS_TOKEN, rounds);
        return this;
    }

    public String toString() {
        return this.json;
    }


    private static String loadJson(String pathToJson, String responseName) {
        StringBuilder stringBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(pathToJson + responseName + ".json"), StandardCharsets.UTF_8)) {
            stream.forEach(s -> stringBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }
}
