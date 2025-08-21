package com.amin.battlearena.economy;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.events.BattleEnded;
import com.amin.battlearena.events.CharacterKilled;
import com.amin.battlearena.model.Character;
import com.amin.battlearena.player.Player;

/**
 * Awards gold for kills and victory.
 *
 * This implementation avoids assuming Player has addGold(...) — instead it
 * tracks Wallets internally (so you don't need to change Player).
 */
public final class EconomyManager {

    private final GameEngine game;
    private final int goldPerKill;
    private final int goldForWin;

    private final Map<Player, Wallet> wallets = new HashMap<>();

    // Unsubscribe runnables returned by EventBus.subscribe(...)
    private final Runnable killSub;
    private final Runnable endSub;

    public EconomyManager(GameEngine game) {
        this(game, 10, 50); // sensible defaults: adjust as you wish
    }

    public EconomyManager(GameEngine game, int goldPerKill, int goldForWin) {
        this.game = Objects.requireNonNull(game);
        this.goldPerKill = goldPerKill;
        this.goldForWin = goldForWin;

        // ensure both players have wallets tracked (human and AI)
        wallets.put(game.getHuman(), new Wallet());
        wallets.put(game.getAI(), new Wallet());

        // subscribe to events from the GameEngine's EventBus
        // CharacterKilled:
        this.killSub = game.getEventBus().subscribe(CharacterKilled.class, (CharacterKilled evt) -> onCharacterKilled(evt));

        // BattleEnded:
        this.endSub = game.getEventBus().subscribe(BattleEnded.class, (BattleEnded evt) -> onBattleEnded(evt));
    }

    private void onCharacterKilled(CharacterKilled evt) {
        if (evt == null) return;
        Player killer = evt.getKiller();
        Character victim = evt.getVictim();

        if (killer == null) {
            game.log("[Economy] kill recorded but killer is null for victim: " + (victim == null ? "unknown" : victim.getName()));
            return;
        }

        Wallet w = wallets.computeIfAbsent(killer, k -> new Wallet());
        w.add(goldPerKill);
        game.log(killer.getName() + " +" + goldPerKill + "g (kill: " + (victim == null ? "unknown" : victim.getName()) + ")");
    }

    private void onBattleEnded(BattleEnded evt) {
        if (evt == null) return;
        // NOTE: BattleEnded exposes public fields `winner` and `loser`
        Player winner = evt.winner; // field access (BattleEnded defines public final Player winner;)
        if (winner == null) return;

        Wallet w = wallets.computeIfAbsent(winner, k -> new Wallet());
        w.add(goldForWin);
        game.log(winner.getName() + " +" + goldForWin + "g (victory bonus)");
    }

    /** Returns the wallet tracked for a player (may create one if absent). */
    public Wallet walletOf(Player p) {
        return wallets.computeIfAbsent(p, k -> new Wallet());
    }

    /** Unsubscribe from events and clean up. */
    public void close() {
        if (killSub != null) try { killSub.run(); } catch (Exception ignored) {}
        if (endSub != null) try { endSub.run(); } catch (Exception ignored) {}
    }
}
