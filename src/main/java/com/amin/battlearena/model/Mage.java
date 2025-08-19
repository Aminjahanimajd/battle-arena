package com.amin.battlearena.model;

import com.amin.battlearena.exceptions.DeadCharacterException;
import com.amin.battlearena.exceptions.InvalidActionException;

public class Mage extends Character {

    private int mana;

    public Mage(String name, int hp, int attack, int defense, int mana, Position position) {
        super(name, hp, attack, defense, mana , position);
        this.mana = mana;
    }

    public int getMana() {
        return mana;
    }
    public int baseDamage() {
        return getAttack() + (mana / 5); // example polymorphic damage
    }
    public void castSpell(Character target) throws InvalidActionException, DeadCharacterException {
        if (this.isDead()) throw new DeadCharacterException(this.getName() + " is dead!");
        if (mana < 10) throw new InvalidActionException("Not enough mana to cast a spell!");
        
        target.takeDamage(this.getAttack() + 5); // Spell does extra damage
        this.mana -= 10;
    }
}
