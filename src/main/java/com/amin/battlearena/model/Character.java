package com.amin.battlearena.model;

import com.amin.battlearena.exceptions.DeadCharacterException;
import com.amin.battlearena.exceptions.InvalidActionException;

// Abstraction & Inheritance: base character; subclasses provide damage profile (polymorphism).
public abstract class Character {
    private final String name;
    private final Stats stats;
    private Position position;

    protected Character(String name, Stats stats, Position start) {
        this.name = name;
        this.stats = stats;
        this.position = start;
    }

    public String getName() { return name; }
    public Stats getStats() { return stats; }
    public Position getPosition() { return position; }
    protected void setPosition(Position p) { this.position = p; }

    public boolean isAlive() { return !stats.isDead(); }

    public boolean inRangeOf(Character target) {
        return this.position.distanceTo(target.position) <= stats.getRange();
    }

    // Template method: shared validation; subclass contributes baseDamage() (polymorphism).
    public void attack(Character target) throws InvalidActionException, DeadCharacterException {
        if (!this.isAlive()) throw new DeadCharacterException(name + " is dead.");
        if (!target.isAlive()) throw new InvalidActionException("Target already defeated.");
        if (!inRangeOf(target)) throw new InvalidActionException("Target out of range.");

        int raw = stats.getAttack() + baseDamage();
        int mitigated = Math.max(0, raw - target.stats.getDefense());
        target.stats.damage(mitigated);
    }

    protected abstract int baseDamage(); // polymorphic contribution
}