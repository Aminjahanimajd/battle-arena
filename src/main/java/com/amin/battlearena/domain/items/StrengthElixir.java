package com.amin.battlearena.domain.items;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;

public final class StrengthElixir implements Consumable {
    private final int attackBoost;
    private final int duration;

    public StrengthElixir(int attackBoost, int duration) {
        this.attackBoost = Math.max(1, attackBoost);
        this.duration = Math.max(1, duration);
    }

    public StrengthElixir() {
        this(5, 3);
    }

    @Override public String key() { return "STRENGTH_ELIXIR_" + attackBoost; }
    @Override public String displayName() { return "Strength Elixir (+" + attackBoost + ")"; }
    @Override public String description() { return "+" + attackBoost + " Attack for " + duration + " turns"; }
    @Override public int getCost() { return attackBoost * 8; }

    @Override
    public void use(GameEngine engine, Character user, Character target) {
        Character receiver = (target != null) ? target : user;
        receiver.getStats().modifyAttack(attackBoost);
        engine.log(user.getName() + " uses Strength Elixir on " + receiver.getName() + " (+" + attackBoost + " Attack)");
    }
}