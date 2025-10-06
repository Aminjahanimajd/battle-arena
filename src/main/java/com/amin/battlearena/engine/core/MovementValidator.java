package com.amin.battlearena.engine.core;

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
        
        // Check movement range using Chebyshev distance (allows diagonal as 1 step)
        Position currentPos = character.getPosition();
        int dx = Math.abs(newPosition.x() - currentPos.x());
        int dy = Math.abs(newPosition.y() - currentPos.y());
        int steps = Math.max(dx, dy);
        int maxMove = getMovementRange(character);
        if (steps > maxMove) {
            return false;
        }

        // For multi-step movement, ensure path is clear (no passing through other units)
        if (steps > 1) {
            if (!isPathClear(currentPos, newPosition)) {
                return false;
            }
        }

        return true;
    }
    
    /**
     * Get the movement range for a character based on their class.
     */
    private int getMovementRange(Character character) {
        String className = character.getClass().getSimpleName();
        if ("Archer".equals(className)) {
            return 2;
        } else if ("Ranger".equals(className)) {
            return 3;
        } else if ("Master".equals(className)) {
            return 2;
        } else {
            return 1; // Warrior, Knight, Mage
        }
    }

    private boolean isPathClear(Position from, Position to) {
        int dx = Integer.compare(to.x(), from.x()); // -1, 0, 1
        int dy = Integer.compare(to.y(), from.y());
        int x = from.x();
        int y = from.y();
        // step until just before destination
        while (true) {
            x += dx; y += dy;
            if (x == to.x() && y == to.y()) break;
            Position step = new Position(x, y);
            if (!board.isWithinBounds(step)) return false;
            if (board.isPositionOccupied(step, allCharacters)) return false;
        }
        return true;
    }
    
    /**
     * Check if a position is valid for movement.
     */
    public boolean isValidPosition(Position position) {
        return board.isWithinBounds(position) && 
               !board.isPositionOccupied(position, allCharacters);
    }
}
