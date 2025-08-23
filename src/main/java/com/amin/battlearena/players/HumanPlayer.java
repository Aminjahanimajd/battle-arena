package com.amin.battlearena.players;

import java.util.List;
import java.util.Scanner;

import com.amin.battlearena.domain.actions.AttackAction;
import com.amin.battlearena.domain.actions.DefendAction;
import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.infra.InvalidActionException;
import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.domain.model.Position;

/**
 * Simple console-driven human player. Prompts user to choose an acting character and an action.
 * This is intentionally simple and can be replaced by a GUI controller later.
 */
public final class HumanPlayer extends Player {

    public HumanPlayer(String name) {
        super(name);
    }

    @Override
    public void takeTurn(GameEngine engine) {
        List<Character> alive = aliveTeam();
        if (alive.isEmpty()) {
            engine.log(getName() + " has no alive characters.");
            return;
        }

        System.out.println("\n[" + getName() + "] Choose a character to act:");
        for (int i = 0; i < alive.size(); i++) {
            var c = alive.get(i);
            System.out.printf("%d) %s %s%n", i + 1, c.getName(), c);
        }
        System.out.print("Choice (number): ");
        
        try (Scanner sc = new Scanner(System.in)) {
            int idx = Math.max(1, Math.min(alive.size(), sc.nextInt())) - 1;
            Character actor = alive.get(idx);

            System.out.println("Actions: 1) Attack  2) Defend  3) Move");
            System.out.print("Choice: ");
            int actionChoice = Math.max(1, Math.min(3, sc.nextInt()));

            try {
                switch (actionChoice) {
                    case 1 -> { // Attack: choose target
                        var enemyAlive = engine.getOpponentOf(this).aliveTeam();
                        if (enemyAlive.isEmpty()) {
                            engine.log("No enemies to attack.");
                            return;
                        }
                        System.out.println("Choose target:");
                        for (int i = 0; i < enemyAlive.size(); i++) {
                            var t = enemyAlive.get(i);
                            System.out.printf("%d) %s %s%n", i + 1, t.getName(), t);
                        }
                        int tIdx = Math.max(1, Math.min(enemyAlive.size(), sc.nextInt())) - 1;
                        new AttackAction().execute(engine, actor, enemyAlive.get(tIdx));
                    }
                    case 2 -> {
                        new DefendAction().execute(engine, actor, null);
                    }
                    case 3 -> {
                        System.out.print("Enter new X: "); int nx = sc.nextInt();
                        System.out.print("Enter new Y: "); int ny = sc.nextInt();
                        Position newPos = new Position(nx, ny);
                        engine.move(actor, newPos);
                    }
                }
            } catch (InvalidActionException | DeadCharacterException ex) {
                engine.log("[Error] " + ex.getMessage());
            } finally {
                // reduce cooldowns/clear temp buffs for this actor
                actor.endTurnHousekeeping();
            }
        }
    }
}
