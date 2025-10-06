package com.amin.battlearena.domain.level;

import java.util.List;

/**
 * Single source of truth for a level configuration.
 * Replaces legacy LevelDefinition/LevelNode split.
 */
public record LevelSpec(
        String id,
        String name,
        List<String> prereqs,
        LevelRewards rewards,
        int requiresPlayerLevel,
        List<String> enemies,
        List<List<Integer>> enemyPositions,
        String note,
        String winConditionType
) {}