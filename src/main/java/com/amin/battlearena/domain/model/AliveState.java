package com.amin.battlearena.domain.model;

import com.amin.battlearena.infra.InvalidActionException;

/**
 * State implementation for alive characters.
 * Characters in this state can perform all normal actions.
 */
public class AliveState implements CharacterState {
    
    private static final AliveState INSTANCE = new AliveState();
    
    private AliveState() {} // Singleton
    
    public static AliveState getInstance() {
        return INSTANCE;
    }
    
    @Override
    public void attack(Character character, Character target) throws InvalidActionException {
        if (character == null || target == null) {
            throw new InvalidActionException("Character and target cannot be null");
        }
        
        // In this state, characters can attack normally
        // The actual attack logic is handled by the GameEngine
        // This method just validates that the action is allowed
    }
    
    @Override
    public void move(Character character, Position newPosition) throws InvalidActionException {
        if (character == null || newPosition == null) {
            throw new InvalidActionException("Character and position cannot be null");
        }
        
        // Perform normal move logic - delegate to character's setPosition method
        character.setPosition(newPosition);
    }
    
    @Override
    public boolean canAct() {
        return true;
    }
    
    @Override
    public String getStateName() {
        return "Alive";
    }
    
    @Override
    public String getStateDescription() {
        return "Character is alive and can perform all actions";
    }
}
