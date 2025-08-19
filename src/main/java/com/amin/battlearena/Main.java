package com.amin.battlearena;

import com.amin.battlearena.actions.AttackAction;
import com.amin.battlearena.actions.DefendAction;
import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.engine.SimpleAIStrategy;
import com.amin.battlearena.model.Mage;
import com.amin.battlearena.model.Position;
import com.amin.battlearena.model.Warrior;

public class Main {

    public static void main(String[] args) {
        // Create player and AI characters
        Warrior player = new Warrior("Hero", 100, 15, 5, new Position(0, 0));
        Mage ai = new Mage("Enemy Mage", 80, 10, 3, 50, new Position(1, 0));


        // Inject AI strategy into GameEngine
        GameEngine engine = new GameEngine(player, ai, new SimpleAIStrategy());

        // Show initial status
        engine.showStatus();

        // Game loop: continue until someone is dead
        while (!engine.isGameOver()) {
            try {
                // Example: player attacks AI
                engine.playerAction(new AttackAction(), ai);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            engine.showStatus();

            // Optionally: player can defend
            try {
                engine.playerAction(new DefendAction(), player);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            engine.showStatus();
        }

        // Print winner
        System.out.println("Winner: " + engine.getWinner().getName());
    }
}
