package com.amin.battlearena.domain.abilities;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.infra.InvalidActionException;
import com.amin.battlearena.domain.model.Character;

/**
 * MasterStrike: big single target hit.
 */
public final class MasterStrike extends AbstractAbility {
    public MasterStrike() { super("Master Strike", "Devastating single hit.", 5); }

    @Override
    public void activate(Character user, Character target, GameEngine engine)
            throws InvalidActionException {
        if (!isReady()) { engine.log(user.getName() + " tried Master Strike but it's on cooldown."); return; }
        if (target == null) throw new InvalidActionException("No target");

        int raw = (int) Math.round(user.getStats().getAttack() * 2.0) + user.getBaseDamage();
        int effective = Math.max(0, raw - (target.getStats().getDefense() + target.getTemporaryDefense()));
        engine.applyDamage(target, effective, user);

        engine.log(user.getName() + " performs Master Strike on " + target.getName() + " for " + raw + " dmg.");
        startCooldown();
    }
}
