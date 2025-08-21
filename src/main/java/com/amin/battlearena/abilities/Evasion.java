package com.amin.battlearena.abilities;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.exceptions.DeadCharacterException;
import com.amin.battlearena.exceptions.InvalidActionException;
import com.amin.battlearena.model.Character;

public final class Evasion extends AbstractAbility {
    public Evasion() { super("Evasion", "Gain +12 temporary defense this turn.", 4); }

    @Override
    public void activate(Character user, Character target, GameEngine engine)
            throws InvalidActionException, DeadCharacterException {
        if (!isReady()) { engine.log(user.getName() + " tried Evasion but it's on cooldown."); return; }
        user.addTemporaryDefense(12);
        engine.log(user.getName() + " activates Evasion (+12 defense).");
        startCooldown();
    }
}
