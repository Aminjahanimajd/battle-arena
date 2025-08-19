package com.amin.battlearena.model;

public final class Warrior extends Character {
    public Warrior(String name, int hp, int attack, int defense, Position position) {
        super(name, hp, attack, defense, position);
    }

    @Override
    protected int baseDamage() {
        return 4;
    }
}