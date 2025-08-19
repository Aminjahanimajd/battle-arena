package com.amin.battlearena.model;

import javax.swing.text.Position;

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

    public abstract int baseDamage();

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public Position getPosition() { return position; }
    protected void setPosition(Position p) { this.position = p; }
    public void takeDamage(int dmg) { this.hp -= dmg; }

    public int getAttack() { return attack; }
    public int getDefense() { return defense; }

    public boolean isAlive() { return !isDead(); }
    public boolean isDead() { return this.hp <= 0; }
    public void addTemporaryDefense(int value) { this.defense += value; }

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