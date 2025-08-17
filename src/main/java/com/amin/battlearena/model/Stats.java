package com.amin.battlearena.model;

// Encapsulation: private fields, controlled updates.
public final class Stats {
    private final int maxHp;
    private int hp;
    private final int attack;
    private final int defense;
    private final int range;

    public Stats(int maxHp, int attack, int defense, int range) {
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.range = range;
    }

    public int getMaxHp() { return maxHp; }
    public int getHp() { return hp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getRange() { return range; }

    public void damage(int amount) { this.hp = Math.max(0, this.hp - amount); }
    public boolean isDead() { return this.hp <= 0; }
}