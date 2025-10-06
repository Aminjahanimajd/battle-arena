package com.amin.battlearena.domain.events;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.players.Player;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Immutable event indicating a character has been killed.
 * Note: We deliberately expose references for performance and identity semantics
 * within the engine; these are treated as read-only by consumers.
 */
public final class CharacterKilled implements GameEvent {
    private final Player killer;
    private final Character victim;

    /**
     * Constructs the event. References are not defensively copied by design.
     */
    @SuppressFBWarnings({
        "EI_EXPOSE_REP2" // Expose internal representation by storing externally mutable objects
    })
    public CharacterKilled(Character victim, Player killer) {
        this.killer = killer;
        this.victim = victim;
    }

    /**
     * Returns the victim character reference (read-only contract).
     */
    @SuppressFBWarnings({
        "EI_EXPOSE_REP" // Expose internal representation by returning field
    })
    public Character getVictim() { return victim; }

    /**
     * Returns the killer player reference (read-only contract).
     */
    @SuppressFBWarnings({
        "EI_EXPOSE_REP" // Expose internal representation by returning field
    })
    public Player getKiller() { return killer; }
}
