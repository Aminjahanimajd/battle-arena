package com.amin.battlearena.domain.items;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;

/** Simple health potion: restores fixed HP to the user (or target if provided). */
public final class HealthPotion implements Consumable {
    private final int healAmount;

    public HealthPotion(int healAmount) {
        this.healAmount = Math.max(1, healAmount);
    }

    @Override public String key() { return "HEALTH_POTION_" + healAmount; }
    @Override public String displayName() { return "Health Potion (" + healAmount + ")"; }
    @Override public String description() { return "Restore " + healAmount + " HP"; }
    @Override public int getCost() { return healAmount + 5; } // Base cost formula

    @Override
    public void use(GameEngine engine, Character user, Character target) {
        Character receiver = (target != null) ? target : user;
        int before = receiver.getStats().getHp();
        receiver.getStats().heal(healAmount);
        int healed = receiver.getStats().getHp() - before;
        engine.log(user.getName() + " uses Health Potion on " + receiver.getName() + " (" + healed + " HP restored)" );
    }
}