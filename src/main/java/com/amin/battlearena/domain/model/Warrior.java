package com.amin.battlearena.domain.model;

import com.amin.battlearena.domain.abilities.PowerStrike;

// Melee fighter: balanced HP/Attack/Defense
public final class Warrior extends Character {

    public Warrior(String name, Position position) {
        super(name, new Stats(100, 12, 8), position, 30, 3, 10);
        addAbility(new PowerStrike());
    }

    @Override
    protected int calculateBaseDamage() {
        return 5; // Increased from 2 for better combat balance
    }

    public int getMovementRange() {
        return 1;
    }
}
