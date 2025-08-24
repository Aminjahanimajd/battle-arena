package com.amin.battlearena.levels;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.amin.battlearena.domain.model.Archer;
import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.domain.model.Knight;
import com.amin.battlearena.domain.model.Mage;
import com.amin.battlearena.domain.model.Master;
import com.amin.battlearena.domain.model.Position;
import com.amin.battlearena.domain.model.Ranger;
import com.amin.battlearena.domain.model.Warrior;

/**
 * Loads level definitions from JSON and creates enemy characters with proper positioning.
 */
public final class LevelLoader {
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    /**
     * Load all level definitions from the JSON file.
     */
    public static List<LevelDefinition> loadLevels() throws IOException {
        try (InputStream is = LevelLoader.class.getResourceAsStream("Levels.json")) {
            if (is == null) {
                throw new IOException("Could not find Levels.json resource");
            }
            
            List<LevelDefinition> levels = MAPPER.readValue(is, new TypeReference<List<LevelDefinition>>() {});
            return levels;
        }
    }
    
    /**
     * Create enemy characters for a specific level.
     */
    public static List<Character> createEnemiesForLevel(int levelNumber) throws IOException {
        List<LevelDefinition> allLevels = loadLevels();
        LevelDefinition level = allLevels.stream()
                .filter(l -> l.level() == levelNumber)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Level " + levelNumber + " not found"));
        
        List<Character> enemies = new ArrayList<>();
        List<String> enemyTypes = level.enemies();
        List<List<Integer>> positions = level.enemyPositions();
        
        for (int i = 0; i < enemyTypes.size(); i++) {
            String enemyType = enemyTypes.get(i);
            List<Integer> pos = positions.get(i);
            Position position = new Position(pos.get(0), pos.get(1));
            
            Character enemy = createEnemy(enemyType, "Enemy-" + (i + 1), position);
            enemies.add(enemy);
        }
        
        return enemies;
    }
    
    /**
     * Create a single enemy character of the specified type.
     */
    private static Character createEnemy(String enemyType, String name, Position position) {
        return switch (enemyType) {
            case "Warrior" -> new Warrior(name, position);
            case "Archer" -> new Archer(name, position);
            case "Mage" -> new Mage(name, position);
            case "Knight" -> new Knight(name, position);
            case "Ranger" -> new Ranger(name, position);
            case "Master" -> new Master(name, position);
            default -> throw new IllegalArgumentException("Unknown enemy type: " + enemyType);
        };
    }
    
    /**
     * Get the starting positions for the player team (left side of the board).
     */
    public static List<Position> getPlayerStartingPositions() {
        return List.of(
            new Position(0, 1), // Warrior
            new Position(0, 2), // Archer  
            new Position(1, 2), // Mage
            new Position(0, 3)  // Knight
        );
    }
}
