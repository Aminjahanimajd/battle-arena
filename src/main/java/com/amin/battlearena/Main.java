package com.amin.battlearena;

import com.amin.battlearena.database.DatabaseManager;
import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.engine.SimpleAIStrategy;
import com.amin.battlearena.model.Archer;
import com.amin.battlearena.model.Board;
import com.amin.battlearena.model.Character;
import com.amin.battlearena.model.Knight;
import com.amin.battlearena.model.Mage;
import com.amin.battlearena.model.Position;
import com.amin.battlearena.model.Ranger;
import com.amin.battlearena.model.Warrior;
import com.amin.battlearena.player.AIPlayer;
import com.amin.battlearena.player.HumanPlayer;

/**
 * Small bootstrap to test the CLI battle loop.
 * - Initializes DB
 * - Builds a human and an AI with simple teams and positions
 * - Runs the engine loop (console-driven)
 */
public final class Main {
    public static void main(String[] args) {
        // initialize database (creates file and tables if missing)
        try {
            DatabaseManager.getConnection();
        } catch (Exception e) {
            System.err.println("[Main] Warning: database initialization failed: " + e.getMessage());
        }

        // create board (example size)
        Board board = new Board(8, 8); // optionally used by UI/engine validation

        // create players
        HumanPlayer human = new HumanPlayer("Amin");
        AIPlayer cpu = new AIPlayer("CPU", new SimpleAIStrategy());

        // build a simple human team
        Warrior w = new Warrior("Ares", new Position(1, 1));
        Archer ar = new Archer("Robin", new Position(1, 2));
        Mage m = new Mage("Gand", new Position(0, 1));
        human.addToTeam(w);
        human.addToTeam(ar);
        human.addToTeam(m);

        // build a CPU team (example: small wave)
        Knight k = new Knight("Bulwark", new Position(6, 6));
        Ranger ranger = new Ranger("Ranger-Boss", new Position(5, 5)); // boss-like archer
        cpu.addToTeam(k);
        cpu.addToTeam(ranger);

        // quick sanity: ensure positions are on board
        for (Character c : human.getTeam()) {
            if (!board.isWithinBounds(c.getPosition()))
                System.err.println("[Main] Warning: human character out of bounds: " + c);
        }
        for (Character c : cpu.getTeam()) {
            if (!board.isWithinBounds(c.getPosition()))
                System.err.println("[Main] Warning: cpu character out of bounds: " + c);
        }

        // create engine and run
        GameEngine engine = new GameEngine(human, cpu , board);
        engine.log("Starting battle on " + board);

        engine.runBattleLoop();

        // close DB connection gracefully
        try {
            DatabaseManager.close();
        } catch (Exception e) {
            System.err.println("[Main] Warning: database close failed: " + e.getMessage());
        }
    }
}
