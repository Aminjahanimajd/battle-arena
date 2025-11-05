package com.amin.battlearena.infra;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;

// Centralized service for validating actions using Chain of Responsibility
public final class ActionValidationService {
    
    private final ActionValidator validationChain;
    
    public ActionValidationService() {
        // Build the validation chain
        this.validationChain = ActionValidator.buildChain(
            new AliveValidator(),
            new TargetValidator(),
            new RangeValidator()
        );
    }
    
    public boolean validateAction(Character actor, Character target, GameEngine engine) {
        if (validationChain == null) {
            return true; // No validators configured
        }
        
        return validationChain.validate(actor, target, engine);
    }
    
    public boolean validateTargetedAction(Character actor, Character target, GameEngine engine) {
        return validateAction(actor, target, engine);
    }
    
    public boolean validateNonTargetedAction(Character actor, GameEngine engine) {
        return validateAction(actor, null, engine);
    }
    
    public ActionValidator getValidationChain() {
        return validationChain;
    }
}
