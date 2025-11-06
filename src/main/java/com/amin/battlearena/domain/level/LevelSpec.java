package com.amin.battlearena.domain.level;

import java.util.List;

// Single source of truth for a level configuration (UI + gameplay data)
public record LevelSpec(
        String id,
        String name,
        String description,
        String chapter,
        int difficulty,
        List<String> prereqs,
        LevelRewards rewards,
        int requiresPlayerLevel,
        List<String> enemies,
        List<List<Integer>> enemyPositions,
        String note
) {
    // Convenience method for enemy count display in UI
    public String getEnemyCountLabel() {
        int count = enemies != null ? enemies.size() : 0;
        if (count == 0) return "None";
        if (count == 1) {
            String enemy = enemies.get(0).toLowerCase();
            if (enemy.contains("dragon") || enemy.contains("boss") || 
                enemy.contains("champion") || enemy.contains("master")) {
                return "1 Boss";
            }
            return "1 Enemy";
        }
        return count + " Enemies";
    }
}