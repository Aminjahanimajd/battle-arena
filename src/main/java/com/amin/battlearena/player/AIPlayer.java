package com.amin.battlearena.player;

import com.amin.battlearena.actions.AttackAction;
import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.model.Character;
import com.amin.battlearena.model.Position;
import java.util.Comparator;
import java.util.List;

public final class AIPlayer extends Player {
    public AIPlayer(String name) { super(name); }

    @Override
    public void takeTurn(GameEngine game) throws Exception {
        List<Character> my = getTeam().stream().filter(Character::isAlive).toList();
        List<Character> enemy = game.getOpponentOf(this).getTeam().stream()
                .filter(Character::isAlive).toList();

        Character attacker = my.get(0);

        Character inRange = enemy.stream()
                .filter(e -> attacker.inRangeOf(e))
                .min(Comparator.comparingInt(e -> e.getStats().getHp()))
                .orElse(null);

        if (inRange != null) {
            new AttackAction().execute(game, attacker, inRange);
            return;
        }

        Character nearest = enemy.stream()
                .min(Comparator.comparingInt(e -> attacker.getPosition().distanceTo(e.getPosition())))
                .orElseThrow();
        Position next = attacker.getPosition().stepTowards(nearest.getPosition());
        game.move(attacker, next);
        game.log(attacker.getName() + " moves to (" + next.x() + "," + next.y() + ")");
    }
}