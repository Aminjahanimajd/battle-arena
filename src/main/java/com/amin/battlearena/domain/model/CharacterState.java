package com.amin.battlearena.domain.model;

import com.amin.battlearena.infra.InvalidActionException;

// State pattern for character behaviors
public interface CharacterState {
    
    void attack(Character character, Character target) throws InvalidActionException;
    
    void move(Character character, Position newPosition) throws InvalidActionException;
    
    boolean canAct();
    
    String getStateName();
    
    String getStateDescription();
}
