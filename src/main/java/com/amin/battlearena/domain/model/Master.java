package com.amin.battlearena.domain.model;

import com.amin.battlearena.domain.abilities.MasterStrike;

/**
 * Boss "Master" — heavy melee champion with very high stats.
 */
public final class Master extends Character {

    public Master(String name, Position position) {
        super(name, new Stats(220, 20, 12, 2), position);
        addAbility(new MasterStrike());
    }

    @Override
    public int baseDamage() {
        return 4;
    }
}
