package com.amin.battlearena.domain.actions;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.infra.InvalidActionException;

/**
 * Represents a domain action (attack, defend, move, skill...). Implementations should be small and focused.
 */
public interface Action {
    /** human-readable action name */
    String name();

    /**
     * Execute the action within the context of a GameEngine.
     * @param engine the engine orchestrating the game
     * @param actor the character performing the action
     * @param target the target character (may be null for non-targeted actions)
     */
    void execute(GameEngine engine, Character actor, Character target)
            throws InvalidActionException, DeadCharacterException;
}
