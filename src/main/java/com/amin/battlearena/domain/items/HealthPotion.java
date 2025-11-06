package com.amin.battlearena.domain.items;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;

public final class HealthPotion extends AbstractConsumable {
    private final int healAmount;

    public HealthPotion(int healAmount) {
        this.healAmount = Math.max(1, healAmount);
    }

    @Override
    public String key() {
        return "HEALTH_POTION_" + healAmount;
    }

    @Override
    public String displayName() {
        return "Health Potion (" + healAmount + ")";
    }

    @Override
    protected String getItemType() {
        return "HEALING";
    }

    @Override
    protected int getBaseValue() {
        return healAmount;
    }

    @Override
    protected String getEffectDescription() {
        return "Restore " + healAmount + " HP";
    }

    @Override
    protected void applyEffect(GameEngine engine, Character user, Character receiver) {
        int before = receiver.getStats().getHp();
        receiver.getStats().heal(healAmount);
        int healed = receiver.getStats().getHp() - before;
        engine.log("  ➜ " + healed + " HP restored");
    }
}