package com.amin.battlearena.domain.items;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;

public final class ShieldScroll extends AbstractConsumable {
    private final int defenseBoost;
    private final int duration;

    public ShieldScroll(int defenseBoost, int duration) {
        this.defenseBoost = Math.max(1, defenseBoost);
        this.duration = Math.max(1, duration);
    }

    public ShieldScroll() {
        this(5, 3);
    }

    @Override
    public String key() {
        return "SHIELD_SCROLL_" + defenseBoost;
    }

    @Override
    public String displayName() {
        return "Shield Scroll (+" + defenseBoost + ")";
    }

    @Override
    protected String getItemType() {
        return "BUFF";
    }

    @Override
    protected int getBaseValue() {
        return defenseBoost;
    }

    @Override
    protected String getEffectDescription() {
        return "+" + defenseBoost + " Defense for " + duration + " turns";
    }

    @Override
    protected void applyEffect(GameEngine engine, Character user, Character receiver) {
        receiver.getStats().modifyDefense(defenseBoost);
        engine.log("  ➜ +" + defenseBoost + " Defense for " + duration + " turns");
    }

    @Override
    protected int calculateCost() {
        return defenseBoost * 7;
    }
}
