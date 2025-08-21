// com/amin/battlearena/events/BattleEnded.java
package com.amin.battlearena.events;

import com.amin.battlearena.player.Player;

public final class BattleEnded implements GameEvent {
    public final Player winner;
    public final Player loser;
    public BattleEnded(Player winner, Player loser) {
        this.winner = winner; this.loser = loser;
    }
}
