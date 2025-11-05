package com.amin.battlearena.engine.memento;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.domain.model.Position;
import com.amin.battlearena.players.Player;

// Memento class for saving and restoring game state
public final class GameMemento {
    
    // Original shallow references kept for backward compatibility/logging
    private final List<Character> humanCharacters;
    private final List<Character> aiCharacters;

    // Deep snapshot by character name -> state
    private final Map<String, CharacterSnapshot> humanSnapshots = new HashMap<>();
    private final Map<String, CharacterSnapshot> aiSnapshots = new HashMap<>();

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

        // Build deep snapshots for safe restoration later
        for (Character c : this.humanCharacters) {
            humanSnapshots.put(c.getName(), CharacterSnapshot.from(c));
        }
        for (Character c : this.aiCharacters) {
            aiSnapshots.put(c.getName(), CharacterSnapshot.from(c));
        }
    }
    
    public List<Character> getHumanCharacters() { return humanCharacters; }
    public List<Character> getAiCharacters() { return aiCharacters; }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("EI_EXPOSE_REP")
    public Map<String, CharacterSnapshot> getHumanSnapshots() { return humanSnapshots; }
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("EI_EXPOSE_REP")
    public Map<String, CharacterSnapshot> getAiSnapshots() { return aiSnapshots; }
    
    public int getCurrentTurn() { return currentTurn; }
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("EI_EXPOSE_REP")
    public Player getCurrentPlayer() { return currentPlayer; }
    public Instant getTimestamp() { return timestamp; }
    
    @Override
    public String toString() {
        return String.format("GameMemento{turn=%d, player=%s, timestamp=%s}", 
                           currentTurn, 
                           currentPlayer != null ? currentPlayer.getName() : "null",
                           timestamp);
    }

    // Minimal immutable snapshot of a character's mutable state for undo/redo
    public static final class CharacterSnapshot {
        public final String name;
        public final int x;
        public final int y;
        public final int hp;
        public final int maxHp;
        public final int attack;
        public final int defense;
        public final int range;

        private CharacterSnapshot(String name, int x, int y, int hp, int maxHp, int attack, int defense, int range) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.hp = hp;
            this.maxHp = maxHp;
            this.attack = attack;
            this.defense = defense;
            this.range = range;
        }

        public static CharacterSnapshot from(Character c) {
            Position p = c.getPosition();
            return new CharacterSnapshot(
                c.getName(),
                p != null ? p.x() : 0,
                p != null ? p.y() : 0,
                c.getStats().getHp(),
                c.getStats().getMaxHp(),
                c.getStats().getAttack(),
                c.getStats().getDefense(),
                c.getStats().getRange()
            );
        }
    }
}
