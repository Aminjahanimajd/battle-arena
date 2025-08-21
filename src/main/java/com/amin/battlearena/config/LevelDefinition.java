package com.amin.battlearena.config;

import java.util.List;

public final class LevelDefinition {
    public int level;
    public List<String> enemies;      // e.g., ["Warrior","Archer","Ranger"]
    public int rewardGold;
    public int requiresLevel;
    public String note;
}
