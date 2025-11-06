package com.amin.battlearena.domain.items;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;

public final class HastePotion extends AbstractConsumable {
    private final int rangeBoost;
    private final int duration;

    public HastePotion(int rangeBoost, int duration) {
        this.rangeBoost = Math.max(1, rangeBoost);
        this.duration = Math.max(1, duration);
    }

    public HastePotion() {
        this(2, 3);
    }

    @Override
    public String key() {
        return "HASTE_POTION_" + rangeBoost;
    }

    @Override
    public String displayName() {
        return "Haste Potion (+" + rangeBoost + ")";
    }

    @Override
    protected String getItemType() {
        return "BUFF";
    }

    @Override
    protected int getBaseValue() {
        return rangeBoost;
    }

    @Override
    protected String getEffectDescription() {
        return "+" + rangeBoost + " Movement Range for " + duration + " turns";
    }

    @Override
    protected void applyEffect(GameEngine engine, Character user, Character receiver) {
        int currentRange = receiver.getStats().getRange();
        receiver.getStats().setRange(currentRange + rangeBoost);
        engine.log("  ➜ +" + rangeBoost + " Movement Range for " + duration + " turns");
    }

    @Override
    protected int calculateCost() {
        return rangeBoost * 10;
    }
}
