package com.amin.battlearena.abilities;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.exceptions.DeadCharacterException;
import com.amin.battlearena.exceptions.InvalidActionException;
import com.amin.battlearena.model.Character;

public final class MasterStrike extends AbstractAbility {
    public MasterStrike() { super("Master Strike", "Devastating blow 220% attack.", 3); }

    @Override
    public void activate(Character user, Character target, GameEngine engine)
            throws InvalidActionException, DeadCharacterException {
        if (!isReady()) { engine.log(user.getName() + " tried Master Strike but it's on cooldown."); return; }
        if (target == null) throw new InvalidActionException("No target");
        int raw = (int)Math.round(user.getStats().getAttack() * 2.2) + user.getBaseDamage();
        target.takeDamage(Math.max(0, raw - (target.getStats().getDefense() + target.getTemporaryDefense())));
        engine.log(user.getName() + " performs Master Strike on " + target.getName() + " for " + raw + " dmg.");
        startCooldown();
    }
}
