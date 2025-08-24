package com.amin.battlearena.domain.model;

import com.amin.battlearena.infra.InvalidActionException;

/**
 * Interface for character states implementing the State pattern.
 * Allows characters to have different behaviors based on their current state.
 */
public interface CharacterState {
    
    /**
     * Attempt to attack a target.
     * @param character the character performing the attack
     * @param target the target character
     * @throws InvalidActionException if the action is not allowed in this state
     */
    void attack(Character character, Character target) throws InvalidActionException;
    
    /**
     * Attempt to move to a new position.
     * @param character the character attempting to move
     * @param newPosition the target position
     * @throws InvalidActionException if the action is not allowed in this state
     */
    void move(Character character, Position newPosition) throws InvalidActionException;
    
    /**
     * Check if the character can perform actions in this state.
     * @return true if the character can act, false otherwise
     */
    boolean canAct();
    
    /**
     * Get the name of this state.
     * @return the state name
     */
    String getStateName();
    
    /**
     * Get the description of this state.
     * @return the state description
     */
    String getStateDescription();
}
