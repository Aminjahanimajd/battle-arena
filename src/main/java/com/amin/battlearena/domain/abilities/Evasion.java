package com.amin.battlearena.domain.abilities;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.infra.InvalidActionException;
import com.amin.battlearena.domain.model.Character;

/**
 * Evasion: small temporary defense boost + a turn-limited dodge chance.
 */
public final class Evasion extends AbstractAbility {
    private static final double DODGE_CHANCE = 0.25; // 25% chance
    private static final int DEFENSE_BONUS = 12;

    public Evasion() { super("Evasion", "Gain +12 temporary defense and a short dodge chance this turn.", 4); }

    @Override
    public void activate(Character user, Character target, GameEngine engine) throws InvalidActionException {
        if (!isReady()) { engine.log(user.getName() + " tried Evasion but it's on cooldown."); return; }
        user.addTemporaryDefense(DEFENSE_BONUS);
        user.addTemporaryEvasion(DODGE_CHANCE);
        engine.log(user.getName() + " activates Evasion (+12 defense, +" + (int)(DODGE_CHANCE*100) + "% dodge).");
        startCooldown();
    }
}
