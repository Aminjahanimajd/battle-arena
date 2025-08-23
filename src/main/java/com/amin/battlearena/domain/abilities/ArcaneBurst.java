package com.amin.battlearena.domain.abilities;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.infra.InvalidActionException;
import com.amin.battlearena.domain.model.Character;

/**
 * ArcaneBurst: damage = 170% of attack + baseDamage
 */
public final class ArcaneBurst extends AbstractAbility {
    public ArcaneBurst() { super("Arcane Burst", "Magic burst dealing 170% attack.", 4); }

    @Override
    public void activate(Character user, Character target, GameEngine engine)
            throws InvalidActionException {
        if (!isReady()) { engine.log(user.getName() + " tried Arcane Burst but it's on cooldown."); return; }
        if (target == null) throw new InvalidActionException("No target");

        int raw = (int) Math.round(user.getStats().getAttack() * 1.7) + user.getBaseDamage();
        int effective = Math.max(0, raw - (target.getStats().getDefense() + target.getTemporaryDefense()));
        engine.applyDamage(target, effective, user);

        engine.log(user.getName() + " casts Arcane Burst on " + target.getName() + " for " + raw + " dmg.");
        startCooldown();
    }
}
