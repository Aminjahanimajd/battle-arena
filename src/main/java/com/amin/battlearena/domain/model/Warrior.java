package com.amin.battlearena.domain.model;

import com.amin.battlearena.domain.abilities.PowerStrike;

/**
 * Melee fighter: balanced HP/Attack/Defense.
 * Movement: 1 space per turn
 * Mana: Low mana pool, slow regeneration
 */
public final class Warrior extends Character {

    public Warrior(String name, Position position) {
        super(name, new Stats(100, 12, 8), position, 30, 3, 10);
        addAbility(new PowerStrike());
    }

    @Override
    protected int calculateBaseDamage() {
        return 5; // Increased from 2 for better combat balance
    }

    /**
     * Warriors can move 1 space per turn
     */
    public int getMovementRange() {
        return 1;
    }
}
