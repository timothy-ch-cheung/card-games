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

public class Util {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String BASE_DIR = "src/test/resources/";

    public static String loadResponse(String responseName) {
        StringBuilder stringBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(BASE_DIR + "responses/" + responseName + ".json"), StandardCharsets.UTF_8)) {
            stream.forEach(s -> stringBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    public static String maskId(String json) {
        try {
            ObjectNode node = (ObjectNode) OBJECT_MAPPER.readTree(json);
            node.put("id", repeat("x", node.get("id").asText().length()));
            json = node.toString();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;

    }
}
