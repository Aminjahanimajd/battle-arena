package com.amin.battlearena.persistence;

import com.amin.battlearena.progression.PlayerProgress;

public interface PlayerRepository {
    void save(PlayerProgress progress);
    PlayerProgress load(String playerId);
}
