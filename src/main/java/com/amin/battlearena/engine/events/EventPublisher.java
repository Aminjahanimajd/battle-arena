package com.amin.battlearena.engine.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.amin.battlearena.domain.events.GameEvent;
import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.players.Player;

// Manages game event publishing and listener management
public final class EventPublisher {
    
    private final List<GameEventListener> listeners = new ArrayList<>();
    
    public void addEventListener(GameEventListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }
    
    public void removeEventListener(GameEventListener listener) {
        listeners.remove(listener);
    }
    
    public void publish(GameEvent event) {
        if (event == null) return;
        
        listeners.forEach(listener -> {
            try {
                if (event instanceof com.amin.battlearena.domain.events.BattleEnded battleEnded) {
                    listener.onBattleEnded(battleEnded.getWinner(), battleEnded.getLoser());
                } else if (event instanceof com.amin.battlearena.domain.events.CharacterKilled characterKilled) {
                    listener.onCharacterKilled(characterKilled.getVictim());
                }
            } catch (Exception e) {
                // Log error but don't stop other listeners
                System.err.println("Error in event listener: " + e.getMessage());
            }
        });
    }
    
    public void notifyBattleEnded(Player winner, Player loser) {
        Objects.requireNonNull(winner, "Winner cannot be null");
        Objects.requireNonNull(loser, "Loser cannot be null");
        
        listeners.forEach(listener -> {
            try {
                listener.onBattleEnded(winner, loser);
            } catch (Exception e) {
                System.err.println("Error in battle ended listener: " + e.getMessage());
            }
        });
    }
    
    public void notifyCharacterKilled(Character character) {
        Objects.requireNonNull(character, "Character cannot be null");
        
        listeners.forEach(listener -> {
            try {
                listener.onCharacterKilled(character);
            } catch (Exception e) {
                System.err.println("Error in character killed listener: " + e.getMessage());
            }
        });
    }
    
    public int getListenerCount() {
        return listeners.size();
    }
    
    public void clearListeners() {
        listeners.clear();
    }
}
