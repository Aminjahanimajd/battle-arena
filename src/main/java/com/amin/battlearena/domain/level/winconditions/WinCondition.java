package com.amin.battlearena.domain.level.winconditions;

import com.amin.battlearena.engine.core.GameEngine;

public interface WinCondition {
    boolean isWin(GameEngine game);
    boolean isLoss(GameEngine game);
}