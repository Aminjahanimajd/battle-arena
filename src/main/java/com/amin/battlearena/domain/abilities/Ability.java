package com.amin.battlearena.domain.abilities;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.infra.InvalidActionException;

// Ability contract
public interface Ability {
    String getName();
    String getDescription();
    int getCooldown();
    int getRemainingCooldown();
    int getManaCost();
    boolean isReady();
    boolean canUse(Character user);
    void reduceCooldown();
    void activate(Character user, Character target, GameEngine engine)
            throws InvalidActionException, DeadCharacterException;
}
