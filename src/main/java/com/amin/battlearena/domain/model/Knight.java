package com.amin.battlearena.domain.model;

import com.amin.battlearena.domain.abilities.Charge;

// Tank/protector with high HP and defense
public final class Knight extends Character {

    public Knight(String name, Position position) {
        super(name, new Stats(150, 12, 10, 1), position, 40, 4, 15);
        addAbility(new Charge());
    }

    @Override
    protected int calculateBaseDamage() {
        return 4; // Increased from 3 for better combat balance
    }

    public int getMovementRange() {
        return 1;
    }
}
