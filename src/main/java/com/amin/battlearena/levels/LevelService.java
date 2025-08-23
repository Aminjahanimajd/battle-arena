package com.amin.battlearena.levels;

import java.util.List;
import java.util.Optional;

import com.amin.battlearena.progression.LevelNode;
import com.amin.battlearena.progression.PlayerProgress;

/**
 * Cohesive entry point for level metadata and unlock logic.
 */
public interface LevelService {
    List<LevelNode> unlockedNodes(PlayerProgress progress);
    Optional<LevelNode> findNode(String id);
}
