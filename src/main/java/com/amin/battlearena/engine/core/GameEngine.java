package com.amin.battlearena.engine.core;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import com.amin.battlearena.domain.events.BattleEnded;
import com.amin.battlearena.domain.model.Board;
import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.domain.model.Position;
import com.amin.battlearena.engine.events.EventPublisher;
import com.amin.battlearena.engine.events.GameEventListener;
import com.amin.battlearena.engine.memento.GameCaretaker;
import com.amin.battlearena.engine.random.DefaultRandomProvider;
import com.amin.battlearena.engine.random.RandomProvider;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.players.AIPlayer;
import com.amin.battlearena.players.HumanPlayer;
import com.amin.battlearena.players.Player;

/**
 * Central game engine responsible for coordinating game components.
 * Now follows Single Responsibility Principle by delegating to specialized components.
 *
 * Responsibilities:
 * - Coordinates game components
 * - Provides game services to domain logic
 * - Manages damage application and character state
 */
public final class GameEngine {

    private static final Logger LOG = Logger.getLogger(GameEngine.class.getName());

    private final HumanPlayer human;
    private final AIPlayer ai;
    private final Board board;
    private final TurnManager turnManager;
    private MovementValidator movementValidator;
    private final EventPublisher eventPublisher;
    private final GameState gameState;
    private final RandomProvider randomProvider;

    // Undo/Redo caretaker for game state history
    private final GameCaretaker caretaker = new GameCaretaker();

    public GameEngine(HumanPlayer human, AIPlayer ai, Board board) {
        this(human, ai, board, new DefaultRandomProvider());
    }

    public GameEngine(HumanPlayer human, AIPlayer ai, Board board, RandomProvider randomProvider) {
        this.human = Objects.requireNonNull(human, "human");
        this.ai = Objects.requireNonNull(ai, "ai");
        this.board = Objects.requireNonNull(board, "board");
        this.randomProvider = Objects.requireNonNull(randomProvider, "randomProvider");
        
        // Initialize specialized components
        this.gameState = new GameState();
        this.movementValidator = new MovementValidator(board, gameState.getAllCharacters());
        this.eventPublisher = new EventPublisher();
        this.turnManager = new TurnManager(List.of(human, ai));
        
        // Setup game state
        setupGameState();
        // Ensure movement validator is aware of all characters
        updateMovementValidator();
    }

    private void setupGameState() {
        // Add players to game state
        gameState.addPlayer(human);
        gameState.addPlayer(ai);
        
        // Add all characters to game state
        human.getTeam().forEach(gameState::addCharacter);
        ai.getTeam().forEach(gameState::addCharacter);
        
        // Update movement validator with current characters
        updateMovementValidator();
    }

    private void updateMovementValidator() {
        // This would need to be updated when characters are added/removed
        // For now, we'll recreate the validator
        this.movementValidator = new MovementValidator(board, gameState.getAllCharacters());
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("EI_EXPOSE_REP")
    public HumanPlayer getHuman() { return human; }
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("EI_EXPOSE_REP")
    public AIPlayer getAI() { return ai; }
    public Board getBoard() { return board; }
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("EI_EXPOSE_REP")
    public GameState getGameState() { return gameState; }
    public TurnManager getTurnManager() { return turnManager; }
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("EI_EXPOSE_REP")
    public GameCaretaker getCaretaker() { return caretaker; }

    /**
     * Main turn loop: delegate to TurnManager.
     */
    public void runBattleLoop() {
        turnManager.runTurns(this);
    }

    /**
     * Move a character to a new position using MovementValidator.
     */
    public boolean move(Character character, Position newPosition) {
        // Extra safety: disallow moving onto alive character even if validator missed it
        if (board.isPositionOccupied(newPosition, gameState.getAllCharacters())) {
            return false;
        }
        boolean success = movementValidator.validateAndMove(character, newPosition);
        if (success) {
            gameState.updatePosition(character, newPosition);
            // Save state after successful move
            try {
                getCaretaker().saveState(this);
            } catch (Throwable t) {
                LOG.log(java.util.logging.Level.WARNING, "Failed to save state after move", t);
            }
        }
        return success;
    }

    /**
     * Get the opponent of a given player.
     */
    public Player getOpponentOf(Player player) {
        if (player == human) return ai;
        if (player == ai) return human;
        throw new IllegalArgumentException("Unknown player: " + player.getName());
    }

    /**
     * Apply damage to a character and handle death events.
     */
    public void applyDamage(Character target, int amount) {
        if (target == null || amount <= 0) {
            LOG.warning(String.format("applyDamage called with non-positive amount: %d for %s", 
                       amount, target != null ? target.getName() : "null"));
            return;
        }

        try {
            target.takeDamage(amount);
            log(String.format("%s takes %d damage (HP now %d/%d)", 
                target.getName(), amount, target.getStats().getHp(), target.getStats().getMaxHp()));

            if (!target.isAlive()) {
                log(target.getName() + " has been slain!");
                eventPublisher.notifyCharacterKilled(target);
                gameState.removeCharacter(target);
            }
            // Save state after successful damage application
            try {
                getCaretaker().saveState(this);
            } catch (Throwable t) {
                LOG.log(java.util.logging.Level.WARNING, "Failed to save state after damage", t);
            }
        } catch (DeadCharacterException e) {
            log(target.getName() + " was killed by the damage!");
            eventPublisher.notifyCharacterKilled(target);
            gameState.removeCharacter(target);
            try {
                getCaretaker().saveState(this);
            } catch (Throwable t) {
                LOG.log(java.util.logging.Level.WARNING, "Failed to save state after death", t);
            }
        }
    }

    /**
     * Log a message to the game log.
     */
    public void log(String message) {
        LOG.log(java.util.logging.Level.INFO, "[Engine] {0}", message);
    }

    /**
     * Get a random number for game mechanics.
     */
    public int getRandom(int min, int max) {
        return randomProvider.nextInt(min, max);
    }

    /**
     * Check if a random event occurs based on probability.
     */
    public boolean randomEvent(double probability) {
        return randomProvider.nextDouble() < probability;
    }

    /**
     * Notify that a battle has ended (called by TurnManager).
     */
    public void notifyBattleEnded(Player winner, Player loser) {
        eventPublisher.notifyBattleEnded(winner, loser);
        try {
            eventPublisher.publish(new BattleEnded(winner, loser));
        } catch (Exception e) {
            LOG.warning(String.format("Error publishing battle ended event: %s", e.getMessage()));
        }
    }

    /**
     * Add an event listener.
     */
    public void addEventListener(GameEventListener listener) {
        eventPublisher.addEventListener(listener);
    }

    /**
     * Remove an event listener.
     */
    public void removeEventListener(GameEventListener listener) {
        eventPublisher.removeEventListener(listener);
    }
}
