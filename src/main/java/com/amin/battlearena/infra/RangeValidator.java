package com.amin.battlearena.infra;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.domain.model.Archer;
import com.amin.battlearena.domain.model.Character;

/**
 * Validates that the target is within range of the actor.
 */
public class RangeValidator extends ActionValidator {
    
    @Override
    public boolean validate(Character actor, Character target, GameEngine engine) {
        // Check if actor is an archer and validate range
        if (actor instanceof Archer archer) {
            if (target == null || !archer.inRangeOf(target)) {
                engine.log("Target is null or out of range for " + actor.getName());
                return false;
            }
        } else {
            // For melee characters, check adjacency (distance <= 1)
            if (actor.getPosition().distanceTo(target.getPosition()) > 1) {
                engine.log("Target " + target.getName() + " is not adjacent to " + actor.getName());
                return false;
            }
        }
        
        return validateNext(actor, target, engine);
    }
}
