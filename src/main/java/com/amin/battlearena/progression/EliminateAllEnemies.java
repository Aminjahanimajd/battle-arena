package com.amin.battlearena.progression;

import com.amin.battlearena.engine.GameEngine;

/**
 * Win condition utility: human wins when all AI characters are dead, and vice-versa.
 * Avoids calling non-existent GameEngine.getAliveCharactersFor(...)
 */
public final class EliminateAllEnemies {

    private EliminateAllEnemies() {}

    public static boolean humanWon(GameEngine game) {
        return game.getAI().getTeam().stream().noneMatch(c -> c.isAlive());
    }

    public static boolean aiWon(GameEngine game) {
        return game.getHuman().getTeam().stream().noneMatch(c -> c.isAlive());
    }
}
