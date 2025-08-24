package com.amin.battlearena.levels;

import java.util.List;

/**
 * Defines a level with enemies, rewards, and positioning.
 */
public final class LevelDefinition {
    public final int level;
    public final List<String> enemies;      // e.g., ["Warrior","Archer","Ranger"]
    public final int rewardGold;
    public final int requiresLevel;
    public final String note;
    public final List<List<Integer>> enemyPositions; // e.g., [[7,1],[7,3]] for x,y coordinates
    
    public LevelDefinition(int level, List<String> enemies, int rewardGold, int requiresLevel, String note, List<List<Integer>> enemyPositions) {
        this.level = level;
        this.enemies = enemies;
        this.rewardGold = rewardGold;
        this.requiresLevel = requiresLevel;
        this.note = note;
        this.enemyPositions = enemyPositions;
    }
    
    // Accessor methods
    public int level() { return level; }
    public List<String> enemies() { return enemies; }
    public int rewardGold() { return rewardGold; }
    public int requiresLevel() { return requiresLevel; }
    public String note() { return note; }
    public List<List<Integer>> enemyPositions() { return enemyPositions; }
}
