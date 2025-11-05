package com.amin.battlearena.domain.events;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.players.Player;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

// Immutable event indicating a character has been killed
public final class CharacterKilled implements GameEvent {
    private final Player killer;
    private final Character victim;

    @SuppressFBWarnings({
        "EI_EXPOSE_REP2" // Expose internal representation by storing externally mutable objects
    })
    public CharacterKilled(Character victim, Player killer) {
        this.killer = killer;
        this.victim = victim;
    }

    @SuppressFBWarnings({
        "EI_EXPOSE_REP" // Expose internal representation by returning field
    })
    public Character getVictim() { return victim; }

    @SuppressFBWarnings({
        "EI_EXPOSE_REP" // Expose internal representation by returning field
    })
    public Player getKiller() { return killer; }
}
