package com.amin.battlearena.level;

import java.util.List;
import java.util.Optional;

import com.amin.battlearena.config.LevelDefinition;
import com.amin.battlearena.config.LevelLoader;
import com.amin.battlearena.engine.SimpleAIStrategy;
import com.amin.battlearena.factory.CharacterFactory;
import com.amin.battlearena.model.Board;
import com.amin.battlearena.model.Position;
import com.amin.battlearena.player.AIPlayer;


/**
 * Responsible for instantiating enemy waves per level (data-driven).
 * Places enemies on the board (right side) attempting to avoid collisions.
 */
public final class LevelRunner {

    private final List<LevelDefinition> levels;

    public LevelRunner() {
        this.levels = LevelLoader.loadAll();
    }

    public Optional<LevelDefinition> getLevel(int levelNumber) {
        return levels.stream().filter(l -> l.level == levelNumber).findFirst();
    }

    /**
     * Build an AIPlayer with enemies placed as per simple placement strategy.
     * Returns an AIPlayer (with SimpleAIStrategy) containing the enemy team.
     */
    public AIPlayer createAiForLevel(int levelNumber, String aiName, Board board) {
        LevelDefinition def = getLevel(levelNumber)
                .orElseThrow(() -> new IllegalArgumentException("Level " + levelNumber + " not found"));

        AIPlayer ai = new AIPlayer(aiName, new SimpleAIStrategy());
        int w = board.getWidth();
        int h = board.getHeight();

        // placement algorithm: spread along the rightmost columns starting from top
        int idx = 0;
        for (String type : def.enemies) {
            int x = Math.max(0, w - 1 - (idx / h));      // layer by columns if many
            int y = idx % h;
            Position pos = new Position(x, y);
            // if occupied, try to nudge left until free
            int attempts = 0;
            while (board.isPositionOccupied(pos, ai.getTeam()) && attempts < w) {
                x = Math.max(0, x - 1);
                pos = new Position(x, y);
                attempts++;
            }
            var enemy = CharacterFactory.create(type, type + "-" + (idx + 1), pos);
            // spawn onto board via engine later; here just add to ai team
            ai.addToTeam(enemy);
            idx++;
        }
        return ai;
    }

    public int levelsCount() { return levels.size(); }
}
