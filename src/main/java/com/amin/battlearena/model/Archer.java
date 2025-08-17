package com.amin.battlearena.model;

public final class Archer extends Character {
    public Archer(String name, Position start) {
        super(name, new Stats(30, 5, 2, 3), start);
    }
    @Override
    protected int baseDamage() { return 2; }
}