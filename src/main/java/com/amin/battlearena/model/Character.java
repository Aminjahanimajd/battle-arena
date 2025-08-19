package com.amin.battlearena.model;


import com.amin.battlearena.exceptions.DeadCharacterException;
import com.amin.battlearena.exceptions.InvalidActionException;

// Abstraction & Inheritance: base character; subclasses provide damage profile (polymorphism).
public abstract class Character {
    protected String name;
    protected int hp;
    protected int attack;
    protected int defense;
    protected Position position;

    public Character(String name, int hp, int attack, int defense, Position position) {
        this.name = name;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.position = position;
    }

    // public abstract int baseDamage(); // Removed duplicate declaration

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getAttack() { return attack; }
    public Stats getStats() { return new Stats(hp, attack); }
    public Position getPosition() { return position; }
    protected void setPosition(Position p) { this.position = p; }

    public int getDefense() { return defense; }
    public void moveTo(Position position) { this.setPosition(position); }
    public boolean isAlive() { return !isDead(); }
    public boolean isDead() { return this.hp <= 0; }
    public void addTemporaryDefense(int amount) { this.defense += amount; }

    public boolean inRangeOf(Character target) {
        // Assuming attack is used as range, or add a 'range' field if needed
        return this.position.distanceTo(target.position) <= this.attack;
    }

    public void attack(Character target) throws InvalidActionException, DeadCharacterException {
        if (!this.isAlive()) throw new DeadCharacterException(name + " is dead.");
        if (!target.isAlive()) throw new InvalidActionException("Target already defeated.");
        if (!inRangeOf(target)) throw new InvalidActionException("Target out of range.");

        int raw = this.attack + baseDamage();
        int mitigated = Math.max(0, raw - target.getDefense());
        target.takeDamage(mitigated);
    }
    protected abstract int baseDamage(); // polymorphic contribution

    public void takeDamage(int damage) throws DeadCharacterException {
        if (hp <= 0) throw new DeadCharacterException(name + " is already dead!");
        hp -= damage;
        if (hp <= 0) hp = 0;
    }

    /** Inner helper to carry stats around */
    public static class Stats {
        private final int hp;
        private final int attack;
        public Stats(int hp, int attack) {
            this.hp = hp;
            this.attack = attack;
        }
        public int getHp() { return hp; }
        public int getAttack() { return attack; }
    }

}