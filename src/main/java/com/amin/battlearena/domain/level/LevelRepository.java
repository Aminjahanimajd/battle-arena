package com.amin.battlearena.domain.level;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

// Loads levels from JSON and integrates runtime-registered levels from LevelFactory
public final class LevelRepository {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final List<LevelSpec> jsonLevels;
    private final Map<String, LevelSpec> byId;

    public LevelRepository() {
        try (InputStream is = LevelRepository.class.getResourceAsStream("/com/amin/battlearena/levels/Levels.json")) {
            if (is == null) throw new IOException("Levels.json not found in resources path");
            this.jsonLevels = MAPPER.readValue(is, new TypeReference<List<LevelSpec>>(){});
            this.byId = new HashMap<>(jsonLevels.stream().collect(Collectors.toMap(LevelSpec::id, s -> s)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load level specs", e);
        }
    }

    // Returns all levels (JSON + runtime-registered from LevelFactory)
    public List<LevelSpec> all() {
        List<LevelSpec> allLevels = new ArrayList<>(jsonLevels);
        
        for (String customId : LevelFactory.getRegisteredLevelIds()) {
            LevelSpec customLevel = LevelFactory.getLevel(customId);
            if (customLevel != null && !byId.containsKey(customId)) {
                allLevels.add(customLevel);
            }
        }
        
        return allLevels;
    }
    
    public LevelSpec require(String id) {
        Objects.requireNonNull(id, "Level ID cannot be null");
        
        LevelSpec s = byId.get(id);
        
        if (s == null) {
            s = LevelFactory.getLevel(id);
        }
        
        if (s == null) {
            throw new IllegalArgumentException("Unknown level id: " + id);
        }
        
        return s;
    }
    
    public boolean exists(String id) {
        if (id == null) return false;
        return byId.containsKey(id) || LevelFactory.isRegistered(id);
    }
    
    public List<LevelSpec> jsonLevelsOnly() {
        return List.copyOf(jsonLevels);
    }
}