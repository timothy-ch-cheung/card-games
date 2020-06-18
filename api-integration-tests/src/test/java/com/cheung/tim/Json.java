package com.cheung.tim;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.repeat;

public class Json {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String BASE_DIR = "src/test/resources/";

    private static final String PLAYER_ID_TOKEN = "${player_id}";

    private String json;

    private Json(String json) {
        this.json = json;
    }

    public static Json Json(String path){
        return new Json(loadResponse(path));
    }

    public Json replacePlayerId(String id){
        this.json = this.json.replace(PLAYER_ID_TOKEN, id);
        return this;
    }

    public String toString(){
        return this.json;
    }



    private static String loadResponse(String responseName) {
        StringBuilder stringBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(BASE_DIR + "responses/" + responseName + ".json"), StandardCharsets.UTF_8)) {
            stream.forEach(s -> stringBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }
}
