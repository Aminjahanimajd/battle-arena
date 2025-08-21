package com.amin.battlearena.content;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.amin.battlearena.progression.Level;

public final class LevelRegistry {
    private static final Map<String, Level> levels = new LinkedHashMap<>();
    public static void register(Level level) { levels.put(level.id(), level); }
    public static Optional<Level> find(String id) { return Optional.ofNullable(levels.get(id)); }
    public static Collection<Level> all() { return levels.values(); }
}
