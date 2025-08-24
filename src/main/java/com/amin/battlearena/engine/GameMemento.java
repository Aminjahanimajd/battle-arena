package com.amin.battlearena.engine;

import java.time.Instant;
import java.util.List;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.players.Player;

/**
 * Memento class for saving and restoring game state.
 * Implements the Memento pattern for undo/redo functionality.
 */
public final class GameMemento {
    
    private final List<Character> humanCharacters;
    private final List<Character> aiCharacters;
    private final int currentTurn;
    private final Player currentPlayer;
    private final Instant timestamp;
    
    public GameMemento(List<Character> humanCharacters, List<Character> aiCharacters, 
                      int currentTurn, Player currentPlayer) {
        this.humanCharacters = List.copyOf(humanCharacters);
        this.aiCharacters = List.copyOf(aiCharacters);
        this.currentTurn = currentTurn;
        this.currentPlayer = currentPlayer;
        this.timestamp = Instant.now();
    }
    
    public List<Character> getHumanCharacters() {
        return humanCharacters;
    }
    
    public List<Character> getAiCharacters() {
        return aiCharacters;
    }
    
    public int getCurrentTurn() {
        return currentTurn;
    }
    
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return String.format("GameMemento{turn=%d, player=%s, timestamp=%s}", 
                           currentTurn, 
                           currentPlayer != null ? currentPlayer.getName() : "null",
                           timestamp);
    }
}
