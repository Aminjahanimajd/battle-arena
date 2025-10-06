package com.amin.battlearena.domain.model;

import com.amin.battlearena.domain.abilities.DoubleShot;

/**
 * Ranged attacker with good mobility.
 * Movement: 2 spaces per turn
 * Mana: Medium mana pool, moderate regeneration
 */
public final class Archer extends Character {

    public Archer(String name, Position position) {
        super(name, new Stats(90, 14, 6, 3), position, 45, 5, 20);
        addAbility(new DoubleShot());
    }

    @Override
    protected int calculateBaseDamage() {
        return 4; // Increased from 2 for better combat balance
    }

    /**
     * Archers can move 2 spaces per turn
     */
    public int getMovementRange() {
        return 2;
    }

    /**
     * Check if target is in range for archer attacks
     */
    public boolean inRangeOf(Character target) {
        if (target == null) return false;
        return getPosition().distanceTo(target.getPosition()) <= getStats().getRange();
    }
}
