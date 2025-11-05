package com.amin.battlearena.engine.ai;

import com.amin.battlearena.engine.core.GameEngine;
import com.amin.battlearena.players.Player;

// Strategy interface for AI implementations
public interface AIStrategy {
    void takeTurn(GameEngine engine, Player aiPlayer) throws Exception;
}
