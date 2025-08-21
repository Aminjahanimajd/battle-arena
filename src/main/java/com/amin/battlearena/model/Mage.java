package com.amin.battlearena.model;

import com.amin.battlearena.abilities.ArcaneBurst;

/**
 * Glass-cannon magic user: higher attack, lower defense.
 */
public final class Mage extends Character {

    public Mage(String name, Position position) {
        super(name, new Stats(75, 18, 4, 2), position);
        addAbility(new ArcaneBurst());
    }

    @Override
    protected int baseDamage() {
        return 3;
    }
}
