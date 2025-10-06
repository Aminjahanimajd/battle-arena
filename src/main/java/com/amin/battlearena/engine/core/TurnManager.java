package com.amin.battlearena.engine.core;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amin.battlearena.players.Player;

/**
 * Manages turn orchestration and player switching.
 * Separates turn management responsibility from GameEngine.
 */
public final class TurnManager {
    
    private static final Logger LOG = Logger.getLogger(TurnManager.class.getName());
    private static final int MAX_TURNS = 1000; // Safety limit
    
    private final List<Player> players;
    private int currentTurn = 0;
    
    public TurnManager(List<Player> players) {
        this.players = players;
    }
    
    /**
     * Run the battle loop until one player has no alive characters.
     */
    public void runTurns(GameEngine engine) {
        LOG.info(String.format("Battle started between %s and %s", 
            players.get(0).getName(), players.get(1).getName()));
        
        while (hasAlivePlayers()) {
            Player currentPlayer = getCurrentPlayer();
            
            try {
                LOG.info(String.format("Turn %d - %s", currentTurn + 1, currentPlayer.getName()));
                currentPlayer.takeTurn(engine);
                
                // Check if someone died during the turn
                if (!hasAlivePlayers()) {
                    break;
                }
                
                // Only increment turn if the current player's turn completed successfully
                currentTurn++;
                
            } catch (Exception | Error t) {
                LOG.log(Level.WARNING, "Exception during player turn: " + currentPlayer.getName(), t);
                // Don't increment turn on error - let the same player try again
                // But add a safety check to prevent infinite loops
                if (currentTurn > MAX_TURNS) {
                    LOG.severe("ERROR: Too many turns, ending battle due to infinite loop protection");
                    break;
                }
            }
        }
        
        Player winner = getWinner();
        Player loser = getLoser();
        
        LOG.info(String.format("Battle ended. Winner: %s, Loser: %s", winner.getName(), loser.getName()));
        engine.notifyBattleEnded(winner, loser);
    }
    
    private boolean hasAlivePlayers() {
        return players.stream().anyMatch(Player::hasAliveCharacters);
    }
    
    private Player getCurrentPlayer() {
        return players.get(currentTurn % players.size());
    }
    
    private Player getWinner() {
        return players.stream()
                .filter(Player::hasAliveCharacters)
                .findFirst()
                .orElse(players.get(1)); // Default to second player if no one is alive
    }
    
    private Player getLoser() {
        return players.stream()
                .filter(p -> !p.hasAliveCharacters())
                .findFirst()
                .orElse(players.get(0)); // Default to first player if no one is dead
    }
    
    public int getCurrentTurn() {
        return currentTurn;
    }
    
    public Player getCurrentPlayerForTurn() {
        return getCurrentPlayer();
    }
}
