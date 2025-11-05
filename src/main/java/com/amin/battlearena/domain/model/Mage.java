package com.amin.battlearena.domain.model;

import com.amin.battlearena.domain.abilities.ArcaneBurst;

// Magical damage dealer with powerful spells
public final class Mage extends Character {

    public Mage(String name, Position position) {
        super(name, new Stats(80, 15, 5, 3), position, 60, 8, 25);
        addAbility(new ArcaneBurst());
    }

    @Override
    protected int calculateBaseDamage() {
        return 6; // Increased from 1 for better combat balance - mages hit hard but are fragile
    }

    public int getMovementRange() {
        return 1;
    }
}
