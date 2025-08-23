package com.amin.battlearena.levels;

import java.util.List;
import java.util.Optional;

import com.amin.battlearena.domain.model.Board;
import com.amin.battlearena.domain.model.Position;
import com.amin.battlearena.domain.model.Units;
import com.amin.battlearena.engine.SimpleAIStrategy;
import com.amin.battlearena.players.AIPlayer;

/**
 * Creates AIPlayer enemy teams for a level using the JSON LevelDefinitions.
 */
public final class EnemySpawner {

    private final List<LevelDefinition> levels;

    public EnemySpawner() {
        this.levels = LevelLoader.loadAll();
    }

    private Optional<LevelDefinition> getLevel(int levelNumber) {
        return levels.stream().filter(l -> l.level == levelNumber).findFirst();
    }

    public AIPlayer createAiForLevel(int levelNumber, String aiName, Board board) {
        LevelDefinition def = getLevel(levelNumber)
                .orElseThrow(() -> new IllegalArgumentException("Level " + levelNumber + " not found"));

        AIPlayer ai = new AIPlayer(aiName, new SimpleAIStrategy());
        int w = board.getWidth();
        int h = board.getHeight();

        int idx = 0;
        for (String type : def.enemies) {
            int x = Math.max(0, w - 1 - (idx / h));  // fill rightmost columns top-down
            int y = idx % h;
            Position pos = new Position(x, y);

            int attempts = 0;
            while (board.isPositionOccupied(pos, ai.getTeam()) && attempts < w) {
                x = Math.max(0, x - 1);
                pos = new Position(x, y);
                attempts++;
            }

            var enemy = Units.spawn(type, type + "-" + (idx + 1), pos);
            ai.addToTeam(enemy);
            idx++;
        }
        return ai;
    }

    public int levelsCount() { return levels.size(); }
}
