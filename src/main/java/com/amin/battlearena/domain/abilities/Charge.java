package com.amin.battlearena.domain.abilities;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.infra.InvalidActionException;
import com.amin.battlearena.domain.model.Character;

/**
 * Charge: close-range heavy strike
 */
public final class Charge extends AbstractAbility {
    public Charge() { super("Charge", "Close-range heavy strike.", 3); }

    @Override
    public void activate(Character user, Character target, GameEngine engine)
            throws InvalidActionException {
        if (!isReady()) { engine.log(user.getName() + " tried Charge but it's on cooldown."); return; }
        if (target == null) throw new InvalidActionException("No target");

        int raw = (int) Math.round(user.getStats().getAttack() * 1.4) + user.getBaseDamage();
        int effective = Math.max(0, raw - (target.getStats().getDefense() + target.getTemporaryDefense()));
        engine.applyDamage(target, effective, user);

        engine.log(user.getName() + " charges " + target.getName() + " for " + raw + " dmg.");
        startCooldown();
    }
}
