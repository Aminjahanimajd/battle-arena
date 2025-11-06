package com.amin.battlearena.domain.items;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;

public final class RevivalToken extends AbstractConsumable {
    private final int reviveHpPercent;

    public RevivalToken(int reviveHpPercent) {
        this.reviveHpPercent = Math.max(10, Math.min(100, reviveHpPercent));
    }

    public RevivalToken() {
        this(50);
    }

    @Override
    public String key() {
        return "REVIVAL_TOKEN_" + reviveHpPercent;
    }

    @Override
    public String displayName() {
        return "Revival Token (" + reviveHpPercent + "%)";
    }

    @Override
    protected String getItemType() {
        return "REVIVAL";
    }

    @Override
    protected int getBaseValue() {
        return reviveHpPercent;
    }

    @Override
    protected String getEffectDescription() {
        return "Revive a fallen ally with " + reviveHpPercent + "% HP";
    }

    @Override
    protected void applyEffect(GameEngine engine, Character user, Character receiver) {
        if (!receiver.isAlive()) {
            int maxHp = receiver.getStats().getMaxHp();
            int reviveHp = (maxHp * reviveHpPercent) / 100;
            receiver.getStats().setHp(reviveHp);
            engine.log("  ➜ " + receiver.getName() + " revived with " + reviveHp + " HP!");
        } else {
            engine.log("  ➜ " + receiver.getName() + " is already alive");
        }
    }

    @Override
    protected int calculateCost() {
        return reviveHpPercent * 2;
    }

    @Override
    public void use(GameEngine engine, Character user, Character target) {
        if (engine == null || user == null) {
            throw new IllegalArgumentException("Engine and user cannot be null");
        }
        
        Character receiver = (target != null) ? target : user;
        
        applyEffect(engine, user, receiver);
        logUsage(engine, user, receiver);
    }
}
