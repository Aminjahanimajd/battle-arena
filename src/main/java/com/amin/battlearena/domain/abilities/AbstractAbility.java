package com.amin.battlearena.domain.abilities;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.infra.InvalidActionException;
import com.amin.battlearena.domain.model.Character;

/**
 * Base class providing cooldown handling and basic helpers.
 */
public abstract class AbstractAbility implements Ability {
    protected final String name;
    protected final String description;
    protected final int cooldown;
    protected int remainingCooldown = 0;

    protected AbstractAbility(String name, String description, int cooldown) {
        this.name = name;
        this.description = description;
        this.cooldown = Math.max(0, cooldown);
    }

    @Override public String getName() { return name; }
    @Override public String getDescription() { return description; }
    @Override public int getCooldown() { return cooldown; }
    @Override public int getRemainingCooldown() { return remainingCooldown; }
    @Override public boolean isReady() { return remainingCooldown == 0; }
    @Override public void reduceCooldown() { if (remainingCooldown > 0) remainingCooldown--; }
    protected void startCooldown() { this.remainingCooldown = cooldown; }

    @Override
    public abstract void activate(Character user, Character target, GameEngine engine)
            throws InvalidActionException, DeadCharacterException;
}
