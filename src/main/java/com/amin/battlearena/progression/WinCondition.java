package com.amin.battlearena.progression;

import com.amin.battlearena.engine.GameEngine;

public interface WinCondition {
    boolean isWin(GameEngine game);
    boolean isLoss(GameEngine game);
}
