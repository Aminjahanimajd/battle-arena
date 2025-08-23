package com.amin.battlearena.domain.abilities;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.infra.InvalidActionException;
import com.amin.battlearena.domain.model.Character;

/**
 * PiercingVolley: multiple hits that may pierce defensive stats.
 */
public final class PiercingVolley extends AbstractAbility {
    public PiercingVolley() { super("Piercing Volley", "Three arrows, each 90% attack.", 4); }

    @Override
    public void activate(Character user, Character target, GameEngine engine)
            throws InvalidActionException {
        if (!isReady()) { engine.log(user.getName() + " tried Piercing Volley but it's on cooldown."); return; }
        if (target == null) throw new InvalidActionException("No target");

        int base = (int) Math.round(user.getStats().getAttack() * 0.9) + user.getBaseDamage();
        for (int i = 0; i < 3 && target.isAlive(); i++) {
            int hit = Math.max(0, base - (target.getStats().getDefense() + target.getTemporaryDefense()));
            engine.applyDamage(target, hit, user);
        }

        engine.log(user.getName() + " uses Piercing Volley on " + target.getName());
        startCooldown();
    }
}
