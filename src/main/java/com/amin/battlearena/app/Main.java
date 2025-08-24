package com.amin.battlearena.app;

import com.amin.battlearena.domain.model.Archer;
import com.amin.battlearena.domain.model.Board;
import com.amin.battlearena.domain.model.Knight;
import com.amin.battlearena.domain.model.Mage;
import com.amin.battlearena.domain.model.Position;
import com.amin.battlearena.domain.model.Warrior;
import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.engine.SimpleAIStrategy;
import com.amin.battlearena.persistence.Schema;
import com.amin.battlearena.players.AIPlayer;
import com.amin.battlearena.players.HumanPlayer;

/**
 * Bootstrap entry point: creates a shared EventBus and wires engine and economy to it.
 */
public final class Main {
    public static void main(String[] args) {
        try {
            Schema.ensure();
        } catch (Exception e) {
            System.err.println("[Main] Warning: could not ensure schema: " + e.getMessage());
        }

        Board board = new Board(8, 5);
        HumanPlayer human = new HumanPlayer("Amin");
        AIPlayer cpu = new AIPlayer("CPU", new SimpleAIStrategy());

        human.addToTeam(new Warrior("Garen", new Position(0, 1)));
        human.addToTeam(new Archer("Ashe", new Position(0, 2)));
        human.addToTeam(new Mage("Ryze", new Position(1, 2)));
        human.addToTeam(new Knight("Braum", new Position(0, 3)));

        cpu.addToTeam(new Warrior("Brute-1", new Position(board.getWidth() - 1, 1)));
        cpu.addToTeam(new Archer("Marks-1", new Position(board.getWidth() - 1, 2)));
        cpu.addToTeam(new Mage("Hexer-1", new Position(board.getWidth() - 2, 2)));
        cpu.addToTeam(new Knight("Guard-1", new Position(board.getWidth() - 1, 3)));

        // shared event bus and progress service (commented out for now)
        // EventBus bus = new EventBus();
        // ProgressService progress = new ProgressService();

        GameEngine engine = new GameEngine(human, cpu, board);
        // EconomyManager econ = new EconomyManager(bus, human, cpu, progress, 10, 50);

        engine.log("Starting battle on board " + board);
        engine.runBattleLoop();
        engine.log("Battle finished.");

        // Clean up resources
        human.cleanup();
        // econ.close();
    }
}
