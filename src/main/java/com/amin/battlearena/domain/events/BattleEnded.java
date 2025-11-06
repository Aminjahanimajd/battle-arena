package com.amin.battlearena.domain.events;

import com.amin.battlearena.players.Player;

public final class BattleEnded implements GameEvent {
    private final Player winner;
    private final Player loser;
    
    public BattleEnded(Player winner, Player loser) {
        this.winner = winner;
        this.loser = loser;
    }
    
    public Player getWinner() {
        return winner;
    }
    
    public Player getLoser() {
        return loser;
    }
}
