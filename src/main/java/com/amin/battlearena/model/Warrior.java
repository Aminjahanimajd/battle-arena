package com.amin.battlearena.model;

public final class Warrior extends Character {
    public Warrior(String name, Position start) {
        super(name, new Stats(40, 6, 3, 1), start);
    }
    @Override
    protected int baseDamage() { return 4; }
}