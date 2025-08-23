package com.amin.battlearena.persistence;

import java.util.Optional;

import com.amin.battlearena.progression.PlayerProgress;

/**
 * Compatibility adapter: keeps the FilePlayerRepository name but uses PlayerDAO (DB) under the hood.
 * This prevents breaking code that previously referenced FilePlayerRepository while delegating to the
 * canonical DB layer already present in the project.
 */
public final class FilePlayerRepository {

    private final PlayerDAO dao = new PlayerDAO();

    /** Save progress for the named player (delegates to PlayerDAO). */
    public void save(String playerName, PlayerProgress progress) {
        dao.createOrUpdatePlayer(playerName, progress.wallet().gold(), 1);
    }

    /** Load progress for the named player or return null when none is present. */
    public PlayerProgress load(String playerName) {
        PlayerProgress p = new PlayerProgress();
        Optional<Integer> gold = dao.findGold(playerName);
        gold.ifPresent(p.wallet()::add);
        return p;
    }
}
