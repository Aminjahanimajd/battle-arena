package com.amin.battlearena.infra;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.domain.model.Character;

/**
 * Chain of Responsibility pattern for action validation.
 * Each validator in the chain can validate a specific aspect of an action.
 */
public abstract class ActionValidator {
    protected ActionValidator next;
    
    public void setNext(ActionValidator next) {
        this.next = next;
    }
    
    /**
     * Validate the action. Return true if valid, false if invalid.
     * If valid, continue to next validator in chain.
     */
    public abstract boolean validate(Character actor, Character target, GameEngine engine);
    
    /**
     * Continue validation with next validator in chain.
     */
    protected boolean validateNext(Character actor, Character target, GameEngine engine) {
        if (next == null) return true;
        return next.validate(actor, target, engine);
    }
    
    /**
     * Build a chain of validators.
     */
    public static class ChainBuilder {
        private ActionValidator first;
        private ActionValidator last;
        
        public ChainBuilder add(ActionValidator validator) {
            if (first == null) {
                first = validator;
                last = validator;
            } else {
                last.setNext(validator);
                last = validator;
            }
            return this;
        }
        
        public ActionValidator build() {
            return first;
        }
    }
}
