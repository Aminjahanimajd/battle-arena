package com.amin.battlearena.progression;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CampaignMap {
    private final Map<String, LevelNode> nodes = new LinkedHashMap<>();

    public void add(LevelNode node) { nodes.put(node.id(), node); }
    public Collection<LevelNode> all() { return nodes.values(); }

    public boolean isUnlocked(PlayerProgress progress, String id) {
        var n = nodes.get(id);
        if (n == null) return false;
        // unlocked if all prereqs are unlocked (i.e., beaten)
        return n.prereqs().isEmpty() || n.prereqs().stream().allMatch(prereq -> progress.unlockedLevels().contains(prereq));
    }

    public List<LevelNode> unlockedNodes(PlayerProgress progress) {
        return nodes.values().stream().filter(n -> isUnlocked(progress, n.id())).toList();
    }
}
