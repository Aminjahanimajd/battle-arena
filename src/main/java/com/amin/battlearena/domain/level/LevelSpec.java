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
}