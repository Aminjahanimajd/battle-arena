package com.amin.battlearena.abilities;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.exceptions.DeadCharacterException;
import com.amin.battlearena.exceptions.InvalidActionException;
import com.amin.battlearena.model.Character;

public final class DoubleShot extends AbstractAbility {
    public DoubleShot() { super("Double Shot", "Two quick shots at 75% attack each.", 3); }

    @Override
    public void activate(Character user, Character target, GameEngine engine)
            throws InvalidActionException, DeadCharacterException {
        if (!isReady()) { engine.log(user.getName() + " tried Double Shot but it's on cooldown."); return; }
        if (target == null) throw new InvalidActionException("No target");
        int one = (int)Math.round(user.getStats().getAttack() * 0.75) + user.getBaseDamage();
        for (int i = 0; i < 2; i++) {
            target.takeDamage(Math.max(0, one - (target.getStats().getDefense() + target.getTemporaryDefense())));
        }
        engine.log(user.getName() + " fires Double Shot at " + target.getName() + " (" + (one*2) + " total).");
        startCooldown();
    }
}
