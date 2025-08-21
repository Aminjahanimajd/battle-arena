package com.amin.battlearena.engine;

import com.amin.battlearena.actions.AttackAction;
import com.amin.battlearena.model.Archer;
import com.amin.battlearena.model.Character;
import com.amin.battlearena.model.Position;
import com.amin.battlearena.player.Player;
import java.util.Comparator;
import java.util.List;

/**
 * A small, deterministic AI:
 * - selects first alive character
 * - if it can attack a reachable enemy (for archer considers range), it attacks weakest in range
 * - otherwise it moves towards nearest enemy (one step)
 */
public final class SimpleAIStrategy implements AIStrategy {

    @Override
    public void takeTurn(GameEngine engine, Player aiPlayer) throws Exception {
        List<Character> myAlive = aiPlayer.aliveTeam();
        if (myAlive.isEmpty()) return;
        Player opponent = engine.getOpponentOf(aiPlayer);
        List<Character> enemyAlive = opponent.aliveTeam();
        if (enemyAlive.isEmpty()) return;

        Character actor = myAlive.get(0); // simple pick
        // try to find an enemy in range (for Archer consider its range)
        Character inRange = enemyAlive.stream()
                .filter(e -> {
                    if (actor instanceof Archer a) return a.inRangeOf(e);
                    return actor.getPosition().distanceTo(e.getPosition()) <= 1; // melee adjacency
                })
                .min(Comparator.comparingInt(e -> e.getStats().getHp()))
                .orElse(null);

        if (inRange != null) {
            new AttackAction().execute(engine, actor, inRange);
            actor.endTurnHousekeeping();
            return;
        }

        // move towards nearest enemy
        Character nearest = enemyAlive.stream()
                .min(Comparator.comparingInt(e -> actor.getPosition().distanceTo(e.getPosition())))
                .orElseThrow();
        Position next = actor.getPosition().stepTowards(nearest.getPosition());
        engine.move(actor, next);
        actor.endTurnHousekeeping();
    }
}
