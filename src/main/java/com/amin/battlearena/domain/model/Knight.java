package com.amin.battlearena.domain.model;

import com.amin.battlearena.domain.abilities.Charge;

/**
 * Tank/protector with high HP and defense.
 */
public final class Knight extends Character {

    public Knight(String name, Position position) {
        super(name, new Stats(150, 12, 15, 1), position);
        addAbility(new Charge());
    }

    @Override
    public int baseDamage() {
        return 3;
    }
}
