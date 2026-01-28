package com.amin.battlearena.domain.consumable;

import com.amin.battlearena.domain.character.Character;

public class HealthPotion extends Consumable {
    private int healAmount;

    public HealthPotion(int healAmount) {
        super("Health Potion", "Heals " + healAmount + " HP");
        this.healAmount = healAmount;
    }

    @Override
    public void use(Character target) {
        if (target != null) {
            target.heal(healAmount);
        }
    }
}
