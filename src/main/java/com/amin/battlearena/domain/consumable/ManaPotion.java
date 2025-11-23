package com.amin.battlearena.domain.consumable;

import com.amin.battlearena.domain.character.GameCharacter;

public class ManaPotion extends Consumable {
    private int manaAmount;

    public ManaPotion(int manaAmount) {
        super("Mana Potion", "Restores " + manaAmount + " MP");
        this.manaAmount = manaAmount;
    }

    @Override
    public void use(GameCharacter target) {
        if (target != null) {
            target.restoreMana(manaAmount);
        }
    }
}
