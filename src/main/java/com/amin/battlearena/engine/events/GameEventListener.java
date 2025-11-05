package com.amin.battlearena.engine.events;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.players.Player;

// Interface for game event listeners
public interface GameEventListener {
    
    void onBattleEnded(Player winner, Player loser);
    
    void onCharacterKilled(Character character);
}
