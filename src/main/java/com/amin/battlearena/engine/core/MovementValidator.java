package com.amin.battlearena.engine.core;

import java.util.Collection;
import java.util.Objects;

import com.amin.battlearena.domain.model.Board;
import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.domain.model.Position;

// Validates and executes character movement
public final class MovementValidator {
    
    private final Board board;
    private final Collection<Character> allCharacters;
    
    public MovementValidator(Board board, Collection<Character> allCharacters) {
        this.board = Objects.requireNonNull(board, "Board cannot be null");
        this.allCharacters = Objects.requireNonNull(allCharacters, "Character collection cannot be null");
    }
    
    public boolean validateAndMove(Character character, Position newPosition) {
        if (!canMove(character, newPosition)) {
            return false;
        }
        
        character.setPosition(newPosition);
        return true;
    }
    
    public boolean canMove(Character character, Position newPosition) {
        if (character == null || newPosition == null) {
            return false;
        }
        
        if (!character.isAlive()) {
            return false;
        }
        
        if (!board.isWithinBounds(newPosition)) {
            return false;
        }
        
        if (board.isPositionOccupied(newPosition, allCharacters)) {
            return false;
        }
        
        Position currentPos = character.getPosition();
        int dx = Math.abs(newPosition.x() - currentPos.x());
        int dy = Math.abs(newPosition.y() - currentPos.y());
        int steps = Math.max(dx, dy);
        int maxMove = character.getMovementRange();
        if (steps > maxMove) {
            return false;
        }

        if (steps > 1) {
            if (!isPathClear(currentPos, newPosition)) {
                return false;
            }
        }

        return true;
    }

    private boolean isPathClear(Position from, Position to) {
        int dx = Integer.compare(to.x(), from.x());
        int dy = Integer.compare(to.y(), from.y());
        int x = from.x();
        int y = from.y();
        while (true) {
            x += dx; y += dy;
            if (x == to.x() && y == to.y()) break;
            Position step = new Position(x, y);
            if (!board.isWithinBounds(step)) return false;
            if (board.isPositionOccupied(step, allCharacters)) return false;
        }
        return true;
    }
    
    public boolean isValidPosition(Position position) {
        return board.isWithinBounds(position) && 
               !board.isPositionOccupied(position, allCharacters);
    }
}
