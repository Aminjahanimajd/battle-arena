package com.amin.battlearena.domain.progression;

import java.util.List;
import java.util.Optional;

import com.amin.battlearena.progression.LevelNode;
import com.amin.battlearena.progression.PlayerProgress;

public interface LevelService {
    List<LevelNode> unlockedNodes(PlayerProgress progress);
    Optional<LevelNode> findNode(String id);
}