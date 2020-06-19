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

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
