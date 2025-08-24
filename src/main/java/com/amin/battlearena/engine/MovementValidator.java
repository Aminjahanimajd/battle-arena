package com.amin.battlearena.engine;

import java.util.Collection;
import java.util.Objects;

import com.amin.battlearena.domain.model.Board;
import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.domain.model.Position;

/**
 * Validates and executes character movement.
 * Separates movement validation responsibility from GameEngine.
 */
public final class MovementValidator {
    
    private final Board board;
    private final Collection<Character> allCharacters;
    
    public MovementValidator(Board board, Collection<Character> allCharacters) {
        this.board = Objects.requireNonNull(board, "Board cannot be null");
        this.allCharacters = Objects.requireNonNull(allCharacters, "Character collection cannot be null");
    }
    
    /**
     * Validates and executes movement if valid.
     * @return true if movement was successful, false otherwise
     */
    public boolean validateAndMove(Character character, Position newPosition) {
        if (!canMove(character, newPosition)) {
            return false;
        }
        
        // Execute the move
        character.setPosition(newPosition);
        return true;
    }
    
    /**
     * Checks if a character can move to the specified position.
     */
    public boolean canMove(Character character, Position newPosition) {
        if (character == null || newPosition == null) {
            return false;
        }
        
        // Check if character is alive
        if (!character.isAlive()) {
            return false;
        }
        
        // Check if new position is within board bounds
        if (!board.isWithinBounds(newPosition)) {
            return false;
        }
        
        // Check if new position is occupied
        if (board.isPositionOccupied(newPosition, allCharacters)) {
            return false;
        }
        
        // Check movement range
        Position currentPos = character.getPosition();
        int distance = currentPos.distanceTo(newPosition);
        int maxMove = getMovementRange(character);
        
        return distance <= maxMove;
    }
    
    /**
     * Get the movement range for a character based on their class.
     */
    private int getMovementRange(Character character) {
        String className = character.getClass().getSimpleName();
        return switch (className) {
            case "Archer" -> 2;
            case "Ranger" -> 3;
            case "Master" -> 2;
            default -> 1; // Warrior, Knight, Mage
        };
    }
    
    /**
     * Check if a position is valid for movement.
     */
    public boolean isValidPosition(Position position) {
        return board.isWithinBounds(position) && 
               !board.isPositionOccupied(position, allCharacters);
    }
}
