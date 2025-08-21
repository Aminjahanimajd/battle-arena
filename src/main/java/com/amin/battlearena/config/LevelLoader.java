package com.amin.battlearena.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

public final class LevelLoader {
    private static final ObjectMapper M = new ObjectMapper();

    public static List<LevelDefinition> loadAll() {
        try (InputStream in = LevelLoader.class.getResourceAsStream("/config/levels.json")) {
            if (in == null) throw new IllegalStateException("Missing resource /config/levels.json");
            return M.readValue(in, new TypeReference<List<LevelDefinition>>() {});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
