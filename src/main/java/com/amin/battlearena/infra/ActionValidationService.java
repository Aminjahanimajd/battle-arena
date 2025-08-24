package com.amin.battlearena.infra;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.GameEngine;

/**
 * Service that uses the Chain of Responsibility pattern to validate actions.
 * Provides a centralized way to validate all game actions.
 */
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
    
    /**
     * Validate an action using the validation chain.
     * @param actor the character performing the action
     * @param target the target character (may be null for non-targeted actions)
     * @param engine the game engine
     * @return true if validation passes, false otherwise
     */
    public boolean validateAction(Character actor, Character target, GameEngine engine) {
        if (validationChain == null) {
            return true; // No validators configured
        }
        
        return validationChain.validate(actor, target, engine);
    }
    
    /**
     * Validate an action that requires a target.
     * @param actor the character performing the action
     * @param target the target character
     * @param engine the game engine
     * @return true if validation passes, false otherwise
     */
    public boolean validateTargetedAction(Character actor, Character target, GameEngine engine) {
        return validateAction(actor, target, engine);
    }
    
    /**
     * Validate an action that doesn't require a target.
     * @param actor the character performing the action
     * @param engine the game engine
     * @return true if validation passes, false otherwise
     */
    public boolean validateNonTargetedAction(Character actor, GameEngine engine) {
        return validateAction(actor, null, engine);
    }
    
    /**
     * Get the validation chain for custom validation.
     * @return the first validator in the chain
     */
    public ActionValidator getValidationChain() {
        return validationChain;
    }
}
