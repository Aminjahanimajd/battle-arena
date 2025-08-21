package com.amin.battlearena.model;

import com.amin.battlearena.abilities.PowerStrike;

/**
 * Melee fighter: balanced HP/Attack/Defense.
 */
public final class Warrior extends Character {

    public Warrior(String name, Position position) {
        super(name, new Stats(100, 12, 8, 2), position);
        addAbility(new PowerStrike());
    }

    @Override
    protected int baseDamage() {
        return 2;
    }
}
