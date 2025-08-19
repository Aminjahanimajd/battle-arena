package com.amin.battlearena.model;

import com.amin.battlearena.exceptions.DeadCharacterException;

public final class Archer extends Character {

    public Archer(String name, Position position) {
        // Example stats for Archer: HP = 80, ATK = 15, DEF = 10
        super(name, 80, 15, 10, position);
    }

    @Override
    public void attack(Character target) {
        if (target == null || !target.isAlive()) {
            System.out.println(getName() + " has no valid target to attack.");
            return;
        }

        try {
            // Archers could have a higher chance of dealing bonus damage
            int damage = this.attack + baseDamage();
            target.takeDamage(damage);
            System.out.println(getName() + " shoots an arrow at " + target.getName() +
                    " for " + damage + " damage!");
        } catch (DeadCharacterException e) {
            // Handle defeated target
            System.out.println(target.getName() + " has been slain by " + getName() + "!");
        }
    }

    @Override
    protected int baseDamage() {
        // Archers do reliable ranged damage
        return 2;
    }
}
