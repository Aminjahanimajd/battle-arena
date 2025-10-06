package com.amin.battlearena.infra;

import com.amin.battlearena.domain.model.Archer;
import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.domain.model.Ranger;
import com.amin.battlearena.engine.core.GameEngine;

/**
 * Validator that checks if the target is within range for ranged attacks.
 */
public class RangeValidator extends ActionValidator {
    
    @Override
    public boolean validate(Character actor, Character target, GameEngine engine) {
        if (actor == null || target == null) {
            return validateNext(actor, target, engine);
        }
        
        // Only check range for ranged characters
        if (actor instanceof Archer archer) {
            if (!archer.inRangeOf(target)) {
                if (engine != null) engine.log("Target " + target.getName() + " is out of range for " + actor.getName());
                return false;
            }
        } else if (actor instanceof Ranger ranger) {
            if (!ranger.inRangeOf(target)) {
                if (engine != null) engine.log("Target " + target.getName() + " is out of range for " + actor.getName());
                return false;
            }
        }
        
        return validateNext(actor, target, engine);
    }
}
