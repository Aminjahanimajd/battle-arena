package com.amin.battlearena.engine.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.domain.model.Position;
import com.amin.battlearena.players.Player;

// Manages the current game state including character positions
public class GameState {
    
    private final Set<Character> allCharacters = ConcurrentHashMap.newKeySet();
    private final Map<Position, Character> characterAtPosition = new HashMap<>();
    private final List<Player> players = new ArrayList<>();
    
    public void addCharacter(Character character) {
        if (character != null) {
            allCharacters.add(character);
            updatePosition(character, character.getPosition());
        }
    }
    
    public void removeCharacter(Character character) {
        if (character != null) {
            allCharacters.remove(character);
            characterAtPosition.remove(character.getPosition());
        }
    }
    
    public void updatePosition(Character character, Position newPosition) {
        if (character != null && newPosition != null) {
            // Remove from old position
            characterAtPosition.values().removeIf(c -> c.equals(character));
            // Add to new position
            characterAtPosition.put(newPosition, character);
        }
    }
    
    public boolean isPositionOccupied(Position position) {
        return characterAtPosition.containsKey(position);
    }
    
    public Character getCharacterAt(Position position) {
        return characterAtPosition.get(position);
    }
    
    public Set<Character> getAllCharacters() {
        return Set.copyOf(allCharacters);
    }
    
    public void addPlayer(Player player) {
        if (player != null) {
            players.add(player);
        }
    }
    
    public List<Player> getPlayers() {
        return List.copyOf(players);
    }
    
    public Set<Character> getAliveCharacters() {
        return allCharacters.stream()
                .filter(Character::isAlive)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
    }
    
    public Set<Character> getDeadCharacters() {
        return allCharacters.stream()
                .filter(c -> !c.isAlive())
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
    }

    public void clear() {
        allCharacters.clear();
        characterAtPosition.clear();
        players.clear();
    }

    // Rebuild character collections without touching players
    public void repopulateCharacters(Iterable<Character> characters) {
        allCharacters.clear();
        characterAtPosition.clear();
        if (characters != null) {
            for (Character c : characters) {
                addCharacter(c);
            }
        }
    }
    
    public int getCharacterCount() {
        return allCharacters.size();
    }
    
    public int getPlayerCount() {
        return players.size();
    }
}
