package com.amin.battlearena.domain.level.winconditions;

/** Factory for win conditions by type string. */
public final class WinConditions {
    private WinConditions() {}
    public static WinCondition byType(String type) {
        if ("EliminateAllEnemies".equals(type)) {
            return new EliminateAllEnemiesWinCondition();
        } else {
            throw new IllegalArgumentException("Unknown win condition type: " + type);
        }
    }
}