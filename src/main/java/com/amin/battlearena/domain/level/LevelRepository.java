package com.amin.battlearena.domain.level;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Loads LevelSpec data from JSON resources and provides simple lookups.
 */
public final class LevelRepository {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final List<LevelSpec> all;
    private final Map<String, LevelSpec> byId;

    public LevelRepository() {
        try (InputStream is = LevelRepository.class.getResourceAsStream("/com/amin/battlearena/levels/Levels.json")) {
            if (is == null) throw new IOException("Levels.json not found in resources path");
            this.all = MAPPER.readValue(is, new TypeReference<List<LevelSpec>>(){});
            this.byId = all.stream().collect(Collectors.toMap(LevelSpec::id, s -> s));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load level specs", e);
        }
    }

    public List<LevelSpec> all() { return all; }
    public LevelSpec require(String id) {
        LevelSpec s = byId.get(Objects.requireNonNull(id));
        if (s == null) throw new IllegalArgumentException("Unknown level id: " + id);
        return s;
    }
}