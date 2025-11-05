package com.amin.battlearena.infra;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;

// Validator that checks if the actor is alive before allowing actions
public class AliveValidator extends ActionValidator {
    
    @Override
    public boolean validate(Character actor, Character target, GameEngine engine) {
        if (actor == null) {
            if (engine != null) engine.log("Actor cannot be null");
            return false;
        }
        
        if (!actor.isAlive()) {
            if (engine != null) engine.log("Actor " + actor.getName() + " is dead and cannot act");
            return false;
        }
        
        return validateNext(actor, target, engine);
    }
}
