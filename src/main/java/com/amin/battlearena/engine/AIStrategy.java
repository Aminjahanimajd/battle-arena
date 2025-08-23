package com.amin.battlearena.engine;

import com.amin.battlearena.players.Player;

/**
 * Strategy interface for AI implementations. Implementations perform a full AI turn when invoked.
 */
public interface AIStrategy {
    void takeTurn(GameEngine engine, Player aiPlayer) throws Exception;
}
