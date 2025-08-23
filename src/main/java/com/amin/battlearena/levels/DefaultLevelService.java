package com.amin.battlearena.levels;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.amin.battlearena.progression.CampaignMap;
import com.amin.battlearena.progression.LevelNode;
import com.amin.battlearena.progression.PlayerProgress;

/**
 * Default in-memory implementation backed by an existing CampaignMap.
 */
public final class DefaultLevelService implements LevelService {

    private final CampaignMap map;
    private final Map<String, LevelNode> byId = new LinkedHashMap<>();

    public DefaultLevelService(CampaignMap map) {
        this.map = Objects.requireNonNull(map, "map");
        for (LevelNode n : map.all()) byId.put(n.id(), n);
    }

    @Override
    public List<LevelNode> unlockedNodes(PlayerProgress progress) {
        return map.unlockedNodes(progress);
    }

    @Override
    public Optional<LevelNode> findNode(String id) {
        return Optional.ofNullable(byId.get(id));
    }
}
