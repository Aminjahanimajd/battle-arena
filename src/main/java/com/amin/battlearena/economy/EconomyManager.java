package com.amin.battlearena.economy;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import com.amin.battlearena.domain.events.BattleEnded;
import com.amin.battlearena.domain.events.CharacterKilled;
import com.amin.battlearena.domain.events.EventBus;
import com.amin.battlearena.persistence.ProgressService;
import com.amin.battlearena.players.Player;

/**
 * EconomyManager now follows DIP:
 *  - depends on ProgressService (injected) for persistence
 *  - subscribes to the EventBus for CharacterKilled and BattleEnded
 *
 * Responsibilities:
 *  - Award gold on kills and victory
 *  - Persist wallet state via ProgressService so changes survive process exit
 */
public final class EconomyManager implements AutoCloseable {

    private static final Logger LOG = Logger.getLogger(EconomyManager.class.getName());

    private final ProgressService progressService;
    private final Map<Player, Wallet> wallets = new HashMap<>();
    private final int goldPerKill;
    private final int goldForWin;

    private final Runnable killUnsub;
    private final Runnable endUnsub;

    /**
     * Backwards-compatible ctor that creates a ProgressService internally.
     * Prefer the constructor that accepts ProgressService for testability.
     */
    public EconomyManager(EventBus bus, Player p1, Player p2) {
        this(bus, p1, p2, new ProgressService(), 10, 50);
    }

    /**
     * Preferred constructor: inject ProgressService and tune gold values.
     */
    public EconomyManager(EventBus bus, Player p1, Player p2, ProgressService progressService, int goldPerKill, int goldForWin) {
        this.progressService = Objects.requireNonNull(progressService, "progressService");
        this.goldPerKill = goldPerKill;
        this.goldForWin = goldForWin;

        wallets.put(p1, new Wallet());
        wallets.put(p2, new Wallet());

        // subscribe to events
        this.killUnsub = bus.subscribe(CharacterKilled.class, (CharacterKilled evt) -> onCharacterKilled(evt));
        this.endUnsub  = bus.subscribe(BattleEnded.class, (BattleEnded evt) -> onBattleEnded(evt));

        // Initialize from persisted progress (best-effort).
        try {
            PlayerProgressUtil.loadIntoWallet(progressService, p1, wallets.get(p1));
            PlayerProgressUtil.loadIntoWallet(progressService, p2, wallets.get(p2));
        } catch (Exception e) {
            LOG.fine("Could not pre-load wallet from DB: " + e.getMessage());
        }
    }

    private void onCharacterKilled(CharacterKilled evt) {
        Player killer = evt.getKiller();
        if (killer == null) return;
        Wallet w = walletOf(killer);
        w.add(goldPerKill);
        LOG.info(killer.getName() + " +" + goldPerKill + "g (kill)");

        // persist updated progress for the killer
        try {
            PlayerProgressUtil.saveFromWallet(progressService, killer, w);
        } catch (Exception e) {
            LOG.warning("Failed to persist gold after kill for " + killer.getName() + ": " + e.getMessage());
        }
    }

    private void onBattleEnded(BattleEnded evt) {
        Player winner = evt.winner;
        Wallet w = walletOf(winner);
        w.add(goldForWin);
        LOG.info(winner.getName() + " +" + goldForWin + "g (victory)");

        // persist both players' wallets
        try {
            for (Map.Entry<Player, Wallet> e : wallets.entrySet()) {
                Player p = e.getKey();
                Wallet wallet = e.getValue();
                PlayerProgressUtil.saveFromWallet(progressService, p, wallet);
            }
        } catch (Exception e) {
            LOG.warning("Failed to persist gold after battle end: " + e.getMessage());
        }
    }

    public Wallet walletOf(Player p) {
        return wallets.computeIfAbsent(p, k -> new Wallet());
    }

    @Override
    public void close() {
        if (killUnsub != null) try { killUnsub.run(); } catch (Exception ignored) {}
        if (endUnsub != null) try { endUnsub.run(); } catch (Exception ignored) {}
    }
}
