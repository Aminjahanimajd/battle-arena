package com.amin.battlearena.domain.model;

import java.util.Objects;

/**
 * Encapsulates numeric attributes for a Character: HP, attack, defense and speed.
 *
 * Design notes:
 * - Encapsulation / information hiding: fields are private and mutations are through methods.
 * - Single responsibility: this class only manages numeric stats and basic operations (damage/heal/modify).
 * - Safe guards ensure HP never exceeds max HP and never goes below zero.
 */
public final class Stats {

    private int hp;        // current hit points
    private int maxHp;     // maximum hit points
    private int attack;
    private int defense;
    private int speed;
    private int range;     // used by ranged characters like Archer

    public Stats(int maxHp, int attack, int defense, int speed) {
        if (maxHp <= 0) throw new IllegalArgumentException("maxHp must be > 0");
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attack = Math.max(0, attack);
        this.defense = Math.max(0, defense);
        this.speed = Math.max(0, speed);
    }

    public Stats(int maxHp, int attack, int defense, int speed , int range) {
        this(maxHp, attack, defense, speed);
        this.range = Math.max(1, range);
    }

    // ---------- Accessors ----------
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getSpeed() { return speed; }
    public int getRange() { return range; }

    // ---------- Mutators (controlled) ----------

    /**
     * Apply damage to this stats object. HP is reduced by amount (non-negative).
     * HP is floored at 0.
     */
    public void damage(int amount) {
        if (amount <= 0) return;
        hp = Math.max(0, hp - amount);
    }

    /**
     * Heal the character by amount. HP cannot exceed maxHp.
     */
    public void heal(int amount) {
        if (amount <= 0) return;
        hp = Math.min(maxHp, hp + amount);
    }


    
    /**
     * Set current HP directly (clamped between 0 and maxHp).
     */
    public void setHp(int hp) {
        this.hp = Math.max(0, Math.min(maxHp, hp));
    }

    /**
     * Increase the maximum HP by delta (can be negative). If maxHp reduced below 1, it is clamped to 1.
     * Current HP is clamped to new max as well.
     */
    public void setMaxHp(int newMaxHp) {
        this.maxHp = Math.max(1, newMaxHp);
        if (hp > maxHp) hp = maxHp;
    }

    /**
     * Convenience to modify the max HP by a delta (positive or negative).
     */
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

    public void setSpeed(int newSpeed) {
        this.speed = Math.max(0, newSpeed);
    }

    public void modifySpeed(int delta) {
        this.speed = Math.max(0, this.speed + delta);
    }

    public void setRange(int range) {
        this.range = Math.max(1, range);
    }

    // ---------- Queries ----------
    public boolean isDead() { return hp <= 0; }

    @Override
    public String toString() {
        return "Stats{" +
                "hp=" + hp +
                ", maxHp=" + maxHp +
                ", attack=" + attack +
                ", defense=" + defense +
                ", speed=" + speed +
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
               defense == stats.defense &&
               speed == stats.speed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hp, maxHp, attack, defense, speed);
    }
}
