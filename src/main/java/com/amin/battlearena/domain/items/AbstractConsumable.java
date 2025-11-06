package com.amin.battlearena.domain.items;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;

// Abstract base class for consumable items with shared logic
public abstract class AbstractConsumable implements Consumable {
    
    protected abstract String getItemType();
    
    protected abstract int getBaseValue();
    
    protected abstract String getEffectDescription();
    
    protected abstract void applyEffect(GameEngine engine, Character user, Character target);
    
    @Override
    public void use(GameEngine engine, Character user, Character target) {
        if (engine == null || user == null) {
            throw new IllegalArgumentException("Engine and user cannot be null");
        }
        
        Character receiver = (target != null) ? target : user;
        
        if (!receiver.isAlive()) {
            engine.log(receiver.getName() + " is dead and cannot receive " + displayName());
            return;
        }
        
        applyEffect(engine, user, receiver);
        logUsage(engine, user, receiver);
    }
    
    protected void logUsage(GameEngine engine, Character user, Character receiver) {
        String message = user.getName() + " uses " + displayName() + " on " + receiver.getName();
        engine.log(message);
    }
    
    protected int calculateCost() {
        return getBaseValue() + 5;
    }
    
    @Override
    public int getCost() {
        return calculateCost();
    }
    
    @Override
    public String description() {
        return getEffectDescription();
    }
}
