package com.amin.battlearena.domain.items;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;

/** Simple mana potion: restores fixed mana to the user (or target). */
public final class ManaPotion implements Consumable {
    private final int manaAmount;

    public ManaPotion(int manaAmount) {
        this.manaAmount = Math.max(1, manaAmount);
    }

    @Override public String key() { return "MANA_POTION_" + manaAmount; }
    @Override public String displayName() { return "Mana Potion (" + manaAmount + ")"; }
    @Override public String description() { return "Restore " + manaAmount + " Mana"; }
    @Override public int getCost() { return manaAmount + 3; } // Base cost formula

    @Override
    public void use(GameEngine engine, Character user, Character target) {
        Character receiver = (target != null) ? target : user;
        int before = receiver.getCurrentMana();
        receiver.restoreMana(manaAmount);
        int restored = receiver.getCurrentMana() - before;
        engine.log(user.getName() + " uses Mana Potion on " + receiver.getName() + " (" + restored + " MP restored)" );
    }
}