package com.amin.battlearena.infra;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.domain.model.Character;

/**
 * Validates that the target is valid for the action.
 */
public class TargetValidator extends ActionValidator {
    
    @Override
    public boolean validate(Character actor, Character target, GameEngine engine) {
        if (target == null) {
            engine.log("Target cannot be null");
            return false;
        }
        
        if (!target.isAlive()) {
            engine.log("Target " + target.getName() + " is already dead");
            return false;
        }
        
        if (actor.equals(target)) {
            engine.log("Actor cannot target themselves");
            return false;
        }
        
        return validateNext(actor, target, engine);
    }
}
