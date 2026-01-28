package com.amin.battlearena.domain.consumable;

import com.amin.battlearena.domain.character.Character;

public final class ManaPotion extends Consumable {
    private final int manaAmount;

    public ManaPotion(int manaAmount) {
        super("Mana Potion", "Restores " + manaAmount + " MP");
        this.manaAmount = manaAmount;
    }

    @Override
    public void use(Character target) {
        if (target != null) {
            target.restoreMana(manaAmount);
        }
    }
}
