package com.amin.battlearena.domain.model;

import com.amin.battlearena.domain.abilities.PiercingVolley;

/**
 * Elite ranged attacker with superior mobility and damage.
 * Movement: 3 spaces per turn
 * Mana: High mana pool, fast regeneration
 */
public final class Ranger extends Character {

    public Ranger(String name, Position position) {
        super(name, new Stats(110, 16, 8, 5, 4), position, 55, 6, 30);
        addAbility(new PiercingVolley());
    }

    @Override
    protected int calculateBaseDamage() {
        return 3;
    }

    /**
     * Rangers can move 3 spaces per turn
     */
    public int getMovementRange() {
        return 3;
    }

    /**
     * Check if target is in range for ranger attacks
     */
    public boolean inRangeOf(Character target) {
        if (target == null) return false;
        return getPosition().distanceTo(target.getPosition()) <= getStats().getRange();
    }
}
