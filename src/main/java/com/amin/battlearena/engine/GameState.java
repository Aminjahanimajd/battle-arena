package com.amin.battlearena.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.domain.model.Position;
import com.amin.battlearena.players.Player;

/**
 * Manages the current game state including character positions and game data.
 * Separates state management responsibility from GameEngine.
 */
public final class GameState {
    
    private final Set<Character> allCharacters = ConcurrentHashMap.newKeySet();
    private final Map<Position, Character> characterAtPosition = new HashMap<>();
    private final List<Player> players = new ArrayList<>();
    
    /**
     * Add a character to the game state.
     */
    public void addCharacter(Character character) {
        if (character != null) {
            allCharacters.add(character);
            updatePosition(character, character.getPosition());
        }
    }
    
    /**
     * Remove a character from the game state.
     */
    public void removeCharacter(Character character) {
        if (character != null) {
            allCharacters.remove(character);
            characterAtPosition.remove(character.getPosition());
        }
    }
    
    /**
     * Update a character's position.
     */
    public void updatePosition(Character character, Position newPosition) {
        if (character != null && newPosition != null) {
            // Remove from old position
            characterAtPosition.values().removeIf(c -> c.equals(character));
            // Add to new position
            characterAtPosition.put(newPosition, character);
        }
    }
    
    /**
     * Check if a position is occupied.
     */
    public boolean isPositionOccupied(Position position) {
        return characterAtPosition.containsKey(position);
    }
    
    /**
     * Get the character at a specific position.
     */
    public Character getCharacterAt(Position position) {
        return characterAtPosition.get(position);
    }
    
    /**
     * Get all characters in the game.
     */
    public Set<Character> getAllCharacters() {
        return Set.copyOf(allCharacters);
    }
    
    /**
     * Add a player to the game.
     */
    public void addPlayer(Player player) {
        if (player != null) {
            players.add(player);
        }
    }
    
    /**
     * Get all players in the game.
     */
    public List<Player> getPlayers() {
        return List.copyOf(players);
    }
    
    /**
     * Get all alive characters.
     */
    public Set<Character> getAliveCharacters() {
        return allCharacters.stream()
                .filter(Character::isAlive)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
    }
    
    /**
     * Get all dead characters.
     */
    public Set<Character> getDeadCharacters() {
        return allCharacters.stream()
                .filter(c -> !c.isAlive())
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
    }
    
    /**
     * Clear all game state.
     */
    public void clear() {
        allCharacters.clear();
        characterAtPosition.clear();
        players.clear();
    }
    
    /**
     * Get the number of characters in the game.
     */
    public int getCharacterCount() {
        return allCharacters.size();
    }
    
    /**
     * Get the number of players in the game.
     */
    public int getPlayerCount() {
        return players.size();
    }
}
