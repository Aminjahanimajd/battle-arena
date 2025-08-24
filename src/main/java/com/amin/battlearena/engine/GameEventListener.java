package com.amin.battlearena.engine;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.players.Player;

/**
 * Interface for game event listeners.
 * Provides callbacks for important game events.
 */
public interface GameEventListener {
    
    /**
     * Called when a battle ends.
     * @param winner the winning player
     * @param loser the losing player
     */
    void onBattleEnded(Player winner, Player loser);
    
    /**
     * Called when a character is killed.
     * @param character the character that was killed
     */
    void onCharacterKilled(Character character);
}
