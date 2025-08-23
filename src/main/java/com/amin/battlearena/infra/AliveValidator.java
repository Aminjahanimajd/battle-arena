package com.amin.battlearena.infra;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.domain.model.Character;

/**
 * Validates that the actor is alive and can perform actions.
 */
public class AliveValidator extends ActionValidator {
    
    @Override
    public boolean validate(Character actor, Character target, GameEngine engine) {
        if (!actor.isAlive()) {
            engine.log("Actor " + actor.getName() + " is dead and cannot act");
            return false;
        }
        return validateNext(actor, target, engine);
    }
}
