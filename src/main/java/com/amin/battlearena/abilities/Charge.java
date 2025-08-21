package com.amin.battlearena.abilities;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.exceptions.DeadCharacterException;
import com.amin.battlearena.exceptions.InvalidActionException;
import com.amin.battlearena.model.Character;

public final class Charge extends AbstractAbility {
    public Charge() { super("Charge", "Heavy charge dealing 180% attack.", 4); }

    @Override
    public void activate(Character user, Character target, GameEngine engine)
            throws InvalidActionException, DeadCharacterException {
        if (!isReady()) { engine.log(user.getName() + " tried Charge but it's on cooldown."); return; }
        if (target == null) throw new InvalidActionException("No target");
        int raw = (int)Math.round(user.getStats().getAttack() * 1.8) + user.getBaseDamage();
        target.takeDamage(Math.max(0, raw - (target.getStats().getDefense() + target.getTemporaryDefense())));
        engine.log(user.getName() + " charges " + target.getName() + " for " + raw + " dmg.");
        startCooldown();
    }
}
