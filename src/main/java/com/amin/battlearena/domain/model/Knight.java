package com.amin.battlearena.domain.model;

import com.amin.battlearena.domain.abilities.Charge;

/**
 * Tank/protector with high HP and defense.
 * Movement: 1 space per turn (but Charge ability allows 2 spaces)
 * Mana: Medium mana pool, moderate regeneration
 */
public final class Knight extends Character {

    public Knight(String name, Position position) {
        super(name, new Stats(150, 12, 10, 1), position, 40, 4, 15);
        addAbility(new Charge());
    }

    @Override
    protected int calculateBaseDamage() {
        return 4; // Increased from 3 for better combat balance
    }

    /**
     * Knights can move 1 space per turn normally
     */
    public int getMovementRange() {
        return 1;
    }
}
