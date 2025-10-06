package com.amin.battlearena.infra;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;

/**
 * Validator that checks if the target is valid for the action.
 */
public class TargetValidator extends ActionValidator {
    
    @Override
    public boolean validate(Character actor, Character target, GameEngine engine) {
        if (actor == null) {
            return validateNext(actor, target, engine);
        }
        
        // For actions that require a target
        if (target == null) {
            if (engine != null) engine.log("Action requires a target but none was provided");
            return false;
        }
        
        // Check if target is alive
        if (!target.isAlive()) {
            if (engine != null) engine.log("Target " + target.getName() + " is already dead");
            return false;
        }
        
        // Check if target is the same as actor
        if (target.equals(actor)) {
            if (engine != null) engine.log("Cannot target self");
            return false;
        }
        
        return validateNext(actor, target, engine);
    }
}
