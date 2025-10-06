package com.amin.battlearena.progression;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.amin.battlearena.economy.Wallet;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * PlayerProgress holds the persistent bits of a player's state:
 * - playerId (name)
 * - wallet (gold)
 * - unlockedLevels (set of level IDs such as "L01", "L02")
 *
 * This is a plain data object with mutation helpers used by ProgressService
 * and other higher-level services.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class PlayerProgress implements Serializable {

    private static final long serialVersionUID = 1L;

    private String playerId;
    private final Wallet wallet = new Wallet();
    private final Set<String> unlockedLevels = new HashSet<>();

    public PlayerProgress() { /* empty */ }

    public PlayerProgress(String playerId) {
        this.playerId = Objects.requireNonNull(playerId, "playerId");
    }

    public String playerId() { return playerId; }
    public String getPlayerId() { return playerId; }
    public void setPlayerId(String id) { this.playerId = Objects.requireNonNull(id, "playerId"); }

    public Wallet wallet() { return wallet; }
    public Wallet getWallet() { return wallet; }

    /** Return an immutable view of unlocked levels. */
    public Set<String> unlockedLevels() { return Collections.unmodifiableSet(unlockedLevels); }
    public Set<String> getUnlockedLevels() { return Collections.unmodifiableSet(unlockedLevels); }

    /** Add a level id (e.g., "L03"). */
    public void unlockLevel(String levelId) {
        if (levelId == null || levelId.isBlank()) return;
        unlockedLevels.add(levelId.trim());
    }

    /** Add multiple levels. */
    public void unlockAll(Iterable<String> ids) {
        if (ids == null) return;
        for (String s : ids) if (s != null && !s.isBlank()) unlockedLevels.add(s.trim());
    }

    /** Replace unlocked level set with provided set. */
    public void setUnlockedLevels(Set<String> levels) {
        unlockedLevels.clear();
        if (levels != null) unlockedLevels.addAll(levels);
    }
}
