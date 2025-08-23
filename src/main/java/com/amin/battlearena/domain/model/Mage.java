package com.amin.battlearena.domain.model;

import com.amin.battlearena.domain.abilities.ArcaneBurst;

/**
 * Glass-cannon magic user: higher attack, lower defense.
 */
public final class Mage extends Character {

    public Mage(String name, Position position) {
        super(name, new Stats(75, 18, 4, 2), position);
        addAbility(new ArcaneBurst());
    }

    @Override
    public  int baseDamage() {
        return 3;
    }
}
