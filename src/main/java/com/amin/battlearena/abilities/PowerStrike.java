package com.amin.battlearena.abilities;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.exceptions.DeadCharacterException;
import com.amin.battlearena.exceptions.InvalidActionException;
import com.amin.battlearena.model.Character;

public final class PowerStrike extends AbstractAbility {
    public PowerStrike() { super("Power Strike", "Deliver 150% attack as damage.", 3); }

    @Override
    public void activate(Character user, Character target, GameEngine engine)
            throws InvalidActionException, DeadCharacterException {
        if (!isReady()) {
            engine.log(user.getName() + " tried Power Strike but it's on cooldown.");
            return;
        }
        if (target == null) throw new InvalidActionException("No target");
        int damage = (int) Math.round(user.getStats().getAttack() * 1.5) + user.getBaseDamage();
        target.takeDamage(Math.max(0, damage - (target.getStats().getDefense() + target.getTemporaryDefense())));
        engine.log(user.getName() + " uses Power Strike on " + target.getName() + " for " + damage + " dmg.");
        startCooldown();
    }
}
