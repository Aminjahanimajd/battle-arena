package com.amin.battlearena.domain.abilities;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.infra.InvalidActionException;
import com.amin.battlearena.domain.model.Character;

/**
 * DoubleShot: two quick attacks; each deals ~80% damage.
 */
public final class DoubleShot extends AbstractAbility {
    public DoubleShot() { super("Double Shot", "Two quick arrows.", 2); }

    @Override
    public void activate(Character user, Character target, GameEngine engine)
            throws InvalidActionException {
        if (!isReady()) { engine.log(user.getName() + " tried Double Shot but it's on cooldown."); return; }
        if (target == null) throw new InvalidActionException("No target");

        int one = (int) Math.round(user.getStats().getAttack() * 0.8) + user.getBaseDamage();
        int eff1 = Math.max(0, one - (target.getStats().getDefense() + target.getTemporaryDefense()));
        engine.applyDamage(target, eff1, user);

        // if target still alive, apply the second shot
        if (target.isAlive()) {
            int eff2 = Math.max(0, one - (target.getStats().getDefense() + target.getTemporaryDefense()));
            engine.applyDamage(target, eff2, user);
        }

        engine.log(user.getName() + " fires Double Shot at " + target.getName() + " for approx " + one + " dmg per shot.");
        startCooldown();
    }
}
