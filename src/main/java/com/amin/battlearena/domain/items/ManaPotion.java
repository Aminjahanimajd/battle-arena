package com.amin.battlearena.domain.items;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;

public final class ManaPotion extends AbstractConsumable {
    private final int manaAmount;

    public ManaPotion(int manaAmount) {
        this.manaAmount = Math.max(1, manaAmount);
    }

    @Override
    public String key() {
        return "MANA_POTION_" + manaAmount;
    }

    @Override
    public String displayName() {
        return "Mana Potion (" + manaAmount + ")";
    }

    @Override
    protected String getItemType() {
        return "MANA";
    }

    @Override
    protected int getBaseValue() {
        return manaAmount;
    }

    @Override
    protected String getEffectDescription() {
        return "Restore " + manaAmount + " Mana";
    }

    @Override
    protected void applyEffect(GameEngine engine, Character user, Character receiver) {
        int before = receiver.getCurrentMana();
        receiver.restoreMana(manaAmount);
        int restored = receiver.getCurrentMana() - before;
        engine.log("  ➜ " + restored + " MP restored");
    }

    @Override
    protected int calculateCost() {
        return manaAmount + 3;
    }
}