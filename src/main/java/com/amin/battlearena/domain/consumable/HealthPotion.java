package com.amin.battlearena.domain.consumable;

import com.amin.battlearena.domain.character.GameCharacter;

public class HealthPotion extends Consumable {
    private int healAmount;

    public HealthPotion(int healAmount) {
        super("Health Potion", "Heals " + healAmount + " HP");
        this.healAmount = healAmount;
    }

    @Override
    public void use(GameCharacter target) {
        if (target != null) {
            target.heal(healAmount);
        }
    }
}
