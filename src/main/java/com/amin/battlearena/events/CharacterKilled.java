package com.amin.battlearena.events;

import com.amin.battlearena.model.Character;
import com.amin.battlearena.player.Player;

public final class CharacterKilled implements GameEvent {
    private final Player killer;
    private final Character victim;

    public CharacterKilled(Character victim, Player killer) {
        this.killer = killer; this.victim = victim;
    }

    public Character getVictim() { return victim; }

    public Player getKiller() { return killer; }
}
