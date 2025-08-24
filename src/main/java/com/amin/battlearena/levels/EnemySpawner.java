package com.amin.battlearena.levels;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.amin.battlearena.domain.model.Board;
import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.SimpleAIStrategy;
import com.amin.battlearena.players.AIPlayer;

/**
 * Creates AIPlayer enemy teams for a level using the JSON LevelDefinitions.
 */
public final class EnemySpawner {

    private final List<LevelDefinition> levels;

    public EnemySpawner() {
        try {
            this.levels = LevelLoader.loadLevels();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load levels", e);
        }
    }

    private Optional<LevelDefinition> getLevel(int levelNumber) {
        return levels.stream().filter(l -> l.level == levelNumber).findFirst();
    }

    public AIPlayer createAiForLevel(int levelNumber, String aiName, Board board) {
        // Verify level exists
        if (!getLevel(levelNumber).isPresent()) {
            throw new IllegalArgumentException("Level " + levelNumber + " not found");
        }

        AIPlayer ai = new AIPlayer(aiName, new SimpleAIStrategy());
        
        try {
            // Use the new LevelLoader to create enemies with proper positioning
            List<Character> enemies = LevelLoader.createEnemiesForLevel(levelNumber);
            for (Character enemy : enemies) {
                ai.addToTeam(enemy);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create enemies for level " + levelNumber, e);
        }
        
        return ai;
    }

    public int levelsCount() { return levels.size(); }
}
