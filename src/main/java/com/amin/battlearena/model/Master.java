package com.amin.battlearena.model;

import com.amin.battlearena.abilities.MasterStrike;

/**
 * Boss "Master" — heavy melee champion with very high stats.
 */
public final class Master extends Character {

    public Master(String name, Position position) {
        super(name, new Stats(220, 20, 12, 2), position);
        addAbility(new MasterStrike());
    }

    @Override
    protected int baseDamage() {
        return 4;
    }
}
