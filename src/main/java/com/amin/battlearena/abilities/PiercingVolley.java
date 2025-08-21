package com.amin.battlearena.abilities;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.exceptions.DeadCharacterException;
import com.amin.battlearena.exceptions.InvalidActionException;
import com.amin.battlearena.model.Character;

public final class PiercingVolley extends AbstractAbility {
    public PiercingVolley() { super("Piercing Volley", "Three arrows, each 90% attack.", 3); }

    @Override
    public void activate(Character user, Character target, GameEngine engine)
            throws InvalidActionException, DeadCharacterException {
        if (!isReady()) { engine.log(user.getName() + " tried Piercing Volley but it's on cooldown."); return; }
        if (target == null) throw new InvalidActionException("No target");
        int hit = (int)Math.round(user.getStats().getAttack() * 0.9) + user.getBaseDamage();
        for (int i = 0; i < 3; i++) {
            target.takeDamage(Math.max(0, hit - (target.getStats().getDefense() + target.getTemporaryDefense())));
        }
        engine.log(user.getName() + " fires Piercing Volley at " + target.getName() + " (" + (hit*3) + ").");
        startCooldown();
    }
}
