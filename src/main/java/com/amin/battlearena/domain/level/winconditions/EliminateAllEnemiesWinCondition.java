package com.amin.battlearena.domain.level.winconditions;

import com.amin.battlearena.engine.core.GameEngine;

public final class EliminateAllEnemiesWinCondition implements WinCondition {
    @Override
    public boolean isWin(GameEngine game) {
        return game.getAI().getTeam().stream().noneMatch(c -> c.isAlive());
    }

    @Override
    public boolean isLoss(GameEngine game) {
        return game.getHuman().getTeam().stream().noneMatch(c -> c.isAlive());
    }
}