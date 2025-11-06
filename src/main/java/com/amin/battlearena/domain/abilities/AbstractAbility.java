package com.amin.battlearena.domain.abilities;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.infra.InvalidActionException;

// Base class providing cooldown handling and mana management
public abstract class AbstractAbility implements Ability {
    private final String name ;
    private final String description;
    private final int cooldown;
    private final int manaCost;
    private final int range;
    private int remainingCooldown = 0;

    protected AbstractAbility(String name, String description, int cooldown, int manaCost) {
        this(name, description, cooldown, manaCost, 2); // Default range of 2
    }

    protected AbstractAbility(String name, String description, int cooldown, int manaCost, int range) {
        this.name = name;
        this.description = description;
        this.cooldown = Math.max(0, cooldown);
        this.manaCost = Math.max(0, manaCost);
        this.range = Math.max(1, range);
    }

    @Override public String getName() { return name; }
    @Override public String getDescription() { return description; }
    @Override public int getCooldown() { return cooldown; }
    @Override public int getRemainingCooldown() { return remainingCooldown; }
    @Override public int getManaCost() { return manaCost; }
    @Override public int getRange() { return range; }
    @Override public boolean isReady() { return remainingCooldown == 0; }
    @Override public boolean canUse(Character user) { 
        return isReady() && user.canSpendMana(manaCost); 
    }

    protected boolean isInRange(Character user, Character target) {
        if (user == null || target == null) return false;
        return user.getPosition().distanceTo(target.getPosition()) <= range;
    }
    @Override public void reduceCooldown() { if (remainingCooldown > 0) remainingCooldown--; }
    protected void startCooldown() { this.remainingCooldown = cooldown; }

    @Override
    public abstract void activate(Character user, Character target, GameEngine engine)
            throws InvalidActionException, DeadCharacterException;

    protected void saveStateQuietly(GameEngine engine) {
        try {
            engine.getCaretaker().saveState(engine);
        } catch (Throwable ignored) {}
    }
}
