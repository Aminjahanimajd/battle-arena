package com.amin.battlearena.model;

import com.amin.battlearena.abilities.Charge;

/**
 * Tank/protector with high HP and defense.
 */
public final class Knight extends Character {

    public Knight(String name, Position position) {
        super(name, new Stats(150, 12, 15, 1), position);
        addAbility(new Charge());
    }

    @Override
    protected int baseDamage() {
        return 3;
    }
}
