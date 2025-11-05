package com.amin.battlearena.domain.model;

import com.amin.battlearena.domain.abilities.PiercingVolley;

// Elite ranged attacker with superior mobility and damage
public final class Ranger extends Character {

    public Ranger(String name, Position position) {
        super(name, new Stats(110, 16, 8, 4), position, 55, 6, 30);
        addAbility(new PiercingVolley());
    }

    @Override
    protected int calculateBaseDamage() {
        return 5; // Increased from 3 for better combat balance
    }

    public int getMovementRange() {
        return 3;
    }

    public boolean inRangeOf(Character target) {
        if (target == null) return false;
        return getPosition().distanceTo(target.getPosition()) <= getStats().getRange();
    }
}
