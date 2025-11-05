package com.amin.battlearena.infra;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;

// Chain of Responsibility pattern for action validation
public abstract class ActionValidator {
    
    protected ActionValidator next;
    
    public void setNext(ActionValidator next) {
        this.next = next;
    }
    
    public abstract boolean validate(Character actor, Character target, GameEngine engine);
    
    protected boolean validateNext(Character actor, Character target, GameEngine engine) {
        if (next == null) return true;
        return next.validate(actor, target, engine);
    }
    
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
