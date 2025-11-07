package com.amin.battlearena.domain.model;

import java.util.Objects;

// Encapsulates numeric attributes: HP, attack, defense, range, and critical stats
public final class Stats {

    private int hp;        // current hit points
    private int maxHp;     // maximum hit points
    private int attack;
    private int defense;

    private int range;     // used by ranged characters like Archer

    // Combat extras
    // Critical chance in [0.0, 1.0]
    private double critChance = 0.0;
    // Critical damage multiplier (>= 1.0), default 1.5x
    private double critMultiplier = 1.5;

    public Stats(int maxHp, int attack, int defense) {
        if (maxHp <= 0) throw new IllegalArgumentException("maxHp must be > 0");
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attack = Math.max(0, attack);
        this.defense = Math.max(0, defense);
    }

    public Stats(int maxHp, int attack, int defense, int range) {
        this(maxHp, attack, defense);
        this.range = Math.max(1, range);
    }

    // ---------- Accessors ----------
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }

    public int getRange() { return Math.max(1, range); }

    public double getCritChance() { return Math.max(0.0, Math.min(1.0, critChance)); }
    public double getCritMultiplier() { return Math.max(1.0, critMultiplier); }

    // ---------- Mutators (controlled) ----------

    public void damage(int amount) {
        if (amount <= 0) return;
        hp = Math.max(0, hp - amount);
    }

    public void heal(int amount) {
        if (amount <= 0) return;
        hp = Math.min(maxHp, hp + amount);
    }

    public void setHp(int hp) {
        this.hp = Math.max(0, Math.min(maxHp, hp));
    }

    public void setMaxHp(int newMaxHp) {
        this.maxHp = Math.max(1, newMaxHp);
        if (hp > maxHp) hp = maxHp;
    }

    public void modifyMaxHp(int delta) {
        setMaxHp(this.maxHp + delta);
    }

    public void setAttack(int newAttack) {
        this.attack = Math.max(0, newAttack);
    }

    public void modifyAttack(int delta) {
        this.attack = Math.max(0, this.attack + delta);
    }

    public void setDefense(int newDefense) {
        this.defense = Math.max(0, newDefense);
    }

    public void modifyDefense(int delta) {
        this.defense = Math.max(0, this.defense + delta);
    }

    public void setRange(int range) {
        this.range = Math.max(1, range);
    }

    public void setCritChance(double chance) {
        this.critChance = Math.max(0.0, Math.min(1.0, chance));
    }

    public void addCritChance(double delta) {
        setCritChance(this.critChance + delta);
    }

    public void setCritMultiplier(double multiplier) {
        this.critMultiplier = Math.max(1.0, multiplier);
    }

    // ---------- Queries ----------
    public boolean isDead() { return hp <= 0; }
    
    // Get health as percentage (0.0 to 1.0)
    public double getHealthPercentage() {
        return maxHp > 0 ? (double) hp / maxHp : 0.0;
    }

    @Override
    public String toString() {
        return "Stats{" +
                "hp=" + hp +
                ", maxHp=" + maxHp +
                ", attack=" + attack +
                ", defense=" + defense +
                ", range=" + range +
                ", critChance=" + critChance +
                ", critMultiplier=" + critMultiplier +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stats stats = (Stats) o;
        return hp == stats.hp &&
               maxHp == stats.maxHp &&
               attack == stats.attack &&
               defense == stats.defense;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hp, maxHp, attack, defense);
    }
}