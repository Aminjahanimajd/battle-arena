package com.amin.battlearena.infra;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;

/**
 * Abstract base class for action validators implementing the Chain of Responsibility pattern.
 * Each validator handles a specific validation concern and can pass validation to the next validator.
 */
public abstract class ActionValidator {
    
    protected ActionValidator next;
    
    /**
     * Set the next validator in the chain.
     */
    public void setNext(ActionValidator next) {
        // Defensive: prevent external modifications by accepting only the provided reference
        this.next = next;
    }
    
    /**
     * Validate the action. Subclasses must implement this method.
     * @param actor the character performing the action
     * @param target the target character (may be null for non-targeted actions)
     * @param engine the game engine
     * @return true if validation passes, false otherwise
     */
    public abstract boolean validate(Character actor, Character target, GameEngine engine);
    
    /**
     * Pass validation to the next validator in the chain.
     * @param actor the character performing the action
     * @param target the target character
     * @param engine the game engine
     * @return true if all remaining validators pass, false otherwise
     */
    protected boolean validateNext(Character actor, Character target, GameEngine engine) {
        if (next == null) return true;
        return next.validate(actor, target, engine);
    }
    
    /**
     * Build a chain of validators.
     * @param validators the validators to chain together
     * @return the first validator in the chain
     */
    public static ActionValidator buildChain(ActionValidator... validators) {
        if (validators == null || validators.length == 0) {
            return null;
        }
        
        for (int i = 0; i < validators.length - 1; i++) {
            validators[i].setNext(validators[i + 1]);
        }
        
        return validators[0];
    }
}
