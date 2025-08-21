package com.amin.battlearena.abilities;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.exceptions.DeadCharacterException;
import com.amin.battlearena.exceptions.InvalidActionException;
import com.amin.battlearena.model.Character;

/**
 * Ability contract. Activation can throw domain exceptions (attack may kill).
 */
public interface Ability {
    String getName();
    String getDescription();
    int getCooldown();
    int getRemainingCooldown();
    boolean isReady();
    void reduceCooldown();
    void activate(Character user, Character target, GameEngine engine)
            throws InvalidActionException, DeadCharacterException;
}
