package com.amin.battlearena.players;

import com.amin.battlearena.engine.ai.AIStrategy;
import com.amin.battlearena.engine.core.GameEngine;

/**
 * Simple AI player delegating decision-making to an AIStrategy.
 */
public final class AIPlayer extends Player {

    private final AIStrategy strategy;

    public AIPlayer(String name, AIStrategy strategy) {
        super(name);
        this.strategy = strategy;
    }

    @Override
    public void takeTurn(GameEngine engine) throws Exception {
        strategy.takeTurn(engine, this);
    }
}
