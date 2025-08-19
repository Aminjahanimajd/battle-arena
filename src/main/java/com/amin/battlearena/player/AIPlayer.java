package com.amin.battlearena.player;

import com.amin.battlearena.actions.AttackAction;
import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.model.Character;
import com.amin.battlearena.model.Position;
import com.amin.battlearena.exceptions.InvalidActionException;
import com.amin.battlearena.exceptions.DeadCharacterException;

import java.util.Comparator;
import java.util.List;

/**
 * AI-controlled player for the battle arena.
 * Chooses actions for its characters based on range and HP.
 */
public final class AIPlayer extends Player {

    public AIPlayer(String name) {
        super(name);
    }

    @Override
    public void takeTurn(GameEngine game) throws InvalidActionException, DeadCharacterException {
        // Get alive characters of this player
        List<Character> myTeam = getTeam().stream()
                .filter(Character::isAlive)
                .toList();

        // Get alive characters of the opponent
        Player opponentPlayer = game.getOpponentOf(this);
        if (opponentPlayer == null) return;

        List<Character> enemyTeam = opponentPlayer.getTeam().stream()
                .filter(Character::isAlive)
                .toList();

        if (myTeam.isEmpty() || enemyTeam.isEmpty()) return;

        // Pick first alive character to act
        Character attacker = myTeam.get(0);

        // Find an enemy in range with lowest HP
        Character target = enemyTeam.stream()
                .filter(e -> attacker.inRangeOf(e))
                .min(Comparator.comparingInt(e -> e.getStats().getHp()))
                .orElse(null);

        if (target != null) {
            // Attack target
            new AttackAction().execute(game, attacker, target);
            return;
        }

        // No target in range: move towards nearest enemy
        Character nearestEnemy = enemyTeam.stream()
                .min(Comparator.comparingInt(e -> attacker.getPosition().distanceTo(e.getPosition())))
                .orElseThrow();

        Position nextPosition = attacker.getPosition().stepTowards(nearestEnemy.getPosition());

        game.move(attacker, nextPosition);

        game.log(attacker.getName() + " moves to (" + nextPosition.x() + "," + nextPosition.y() + ")");
    }
}
