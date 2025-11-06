package com.amin.battlearena.domain.events;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.players.Player;

 // Immutable event indicating a character has been killed
public final class CharacterKilled implements GameEvent {
    private final Player killer;
    private final Character victim;

    public CharacterKilled(Character victim, Player killer) {
        this.killer = killer;
        this.victim = victim;
    }

    public Character getVictim() { return victim; }

    public Player getKiller() { return killer; }
}
