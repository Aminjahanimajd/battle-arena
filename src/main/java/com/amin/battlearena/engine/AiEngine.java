package com.amin.battlearena.engine;

import com.amin.battlearena.domain.character.GameCharacter;
import com.amin.battlearena.domain.Tile;
import java.util.List;

public class AiEngine {
    
    public void performTurn(GameEngine engine) {
        List<GameCharacter> chars = engine.getAllCharacters();
        for (GameCharacter c : chars) {
            if (!c.isPlayerTeam() && c.isAlive()) {
                performAction(engine, c);
            }
        }
    }

    private void performAction(GameEngine engine, GameCharacter ai) {
        GameCharacter target = findNearestTarget(engine, ai);
        if (target == null) return;

        // Try to attack first
        if (isInRange(ai, target)) {
            engine.attackCharacter(ai, target, null); // Basic attack
            return;
        }

        // Move towards target
        Tile moveTarget = findMoveTarget(engine, ai, target);
        if (moveTarget == null) return;
        
        engine.moveCharacter(ai, moveTarget);
        
        // Try to attack after move
        if (isInRange(ai, target)) {
            engine.attackCharacter(ai, target, null);
        }
    }

    private GameCharacter findNearestTarget(GameEngine engine, GameCharacter ai) {
        GameCharacter nearest = null;
        int minDist = Integer.MAX_VALUE;
        
        for (GameCharacter c : engine.getAllCharacters()) {
            if (c.isPlayerTeam() && c.isAlive()) {
                int dist = getDistance(ai.getPosition(), c.getPosition());
                if (dist < minDist) {
                    minDist = dist;
                    nearest = c;
                }
            }
        }
        return nearest;
    }

    private Tile findMoveTarget(GameEngine engine, GameCharacter ai, GameCharacter target) {
        // Simple logic: move to a tile that minimizes distance to target
        // but is within movement range and not occupied
        Tile bestTile = null;
        int minDist = Integer.MAX_VALUE;
        
        int startX = ai.getPosition().getX();
        int startY = ai.getPosition().getY();
        int range = ai.getSpeed();
        
        for (int x = startX - range; x <= startX + range; x++) {
            for (int y = startY - range; y <= startY + range; y++) {
                if (!engine.getBoard().isValid(x, y)) continue;
                
                Tile t = engine.getBoard().getTile(x, y);
                if (t.isOccupied()) continue;

                int distToTarget = getDistance(t, target.getPosition());
                int distFromStart = Math.abs(x - startX) + Math.abs(y - startY);
                
                if (distFromStart > range) continue;
                if (distToTarget >= minDist) continue;

                minDist = distToTarget;
                bestTile = t;
            }
        }
        return bestTile;
    }

    private boolean isInRange(GameCharacter attacker, GameCharacter target) {
        return getDistance(attacker.getPosition(), target.getPosition()) <= attacker.getRange();
    }

    private int getDistance(Tile t1, Tile t2) {
        return Math.abs(t1.getX() - t2.getX()) + Math.abs(t1.getY() - t2.getY());
    }
}
