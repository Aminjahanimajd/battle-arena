package com.amin.battlearena.domain.model;

import com.amin.battlearena.infra.InvalidActionException;

// State implementation for dead characters
public class DeadState implements CharacterState {
    
    private static final DeadState INSTANCE = new DeadState();
    
    private DeadState() {} // Singleton
    
    public static DeadState getInstance() {
        return INSTANCE;
    }
    
    @Override
    public void attack(Character character, Character target) throws InvalidActionException {
        throw new InvalidActionException(
            (character != null ? character.getName() : "Character") + " is dead and cannot attack");
    }
    
    @Override
    public void move(Character character, Position newPosition) throws InvalidActionException {
        throw new InvalidActionException(
            (character != null ? character.getName() : "Character") + " is dead and cannot move");
    }
    
    @Override
    public boolean canAct() {
        return false;
    }
    
    @Override
    public String getStateName() {
        return "Dead";
    }
    
    @Override
    public String getStateDescription() {
        return "Character is dead and cannot perform any actions";
    }
}
