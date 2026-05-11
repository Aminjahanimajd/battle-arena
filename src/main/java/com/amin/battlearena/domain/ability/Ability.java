package com.amin.battlearena.domain.ability;

import com.amin.battlearena.domain.character.Character;

// Base class for abilities
public abstract class Ability implements AbilityInterface {
    private final String name;
    private final int manaCost;
    private final int cooldown;
    private final int range;
    private int currentCooldown;

    // Ability constructor
    public Ability(final String name, final int manaCost, final int cooldown, final int range) {
        this.name = name;
        this.manaCost = manaCost;
        this.cooldown = cooldown;
        this.range = range;
        this.currentCooldown = 0;
    }

    // Returns ability name
    @Override
    public final String getName() { return name; }
    // Returns mana cost
    @Override
    public final int getManaCost() { return manaCost; }
    // Returns cooldown
    @Override
    public final int getCooldown() { return cooldown; }
    // Returns range
    @Override
    public final int getRange() { return range; }
    // Returns current cooldown
    @Override
    public final int getCurrentCooldown() { return currentCooldown; }

    // Checks if ability is ready
    @Override
    public final boolean isReady() { return currentCooldown == 0; }

    // Reduces cooldown
    @Override
    public final void reduceCooldown() { if (currentCooldown > 0) currentCooldown--; }

    // Puts ability on cooldown
    @Override
    public final void putOnCooldown() { currentCooldown = cooldown; }

    // Executes ability effect
    @Override
    public abstract void execute(final Character source, final Character target);
}
