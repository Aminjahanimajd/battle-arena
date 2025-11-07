package com.amin.battlearena.economy;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import com.amin.battlearena.domain.events.BattleEnded;
import com.amin.battlearena.domain.events.CharacterKilled;
import com.amin.battlearena.domain.events.EventBus;
import com.amin.battlearena.infra.CharacterBalanceConfig;
import com.amin.battlearena.persistence.PlayerProgressUtil;
import com.amin.battlearena.persistence.ProgressService;
import com.amin.battlearena.players.Player;

// EconomyManager: awards gold on kills and victory, persists wallet state via ProgressService
// NOW USES CharacterBalanceConfig.economy section for gold values (single source of truth)
public final class EconomyManager implements AutoCloseable {

    private static final Logger LOG = Logger.getLogger(EconomyManager.class.getName());

    private final ProgressService progressService;
    private final Map<Player, Wallet> wallets = new HashMap<>();
    private final int goldPerKill;
    private final int goldForWin;

    private final Runnable killUnsub;
    private final Runnable endUnsub;

    // Backwards-compatible ctor that reads gold values from balance.json
    public EconomyManager(EventBus bus, Player p1, Player p2) {
        this(bus, p1, p2, new ProgressService(), 
             CharacterBalanceConfig.getInstance().getGoldPerKill(),
             CharacterBalanceConfig.getInstance().getGoldForWin());
    }

    // Preferred constructor: inject ProgressService and tune gold values
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
            LOG.log(java.util.logging.Level.FINE, "Could not pre-load wallet from DB: {0}", e.getMessage());
        }
    }

    private void onCharacterKilled(CharacterKilled evt) {
        Player killer = evt.getKiller();
        if (killer == null) return;
        Wallet w = walletOf(killer);
        w.add(goldPerKill);
        LOG.log(java.util.logging.Level.INFO, "{0} +{1}g (kill)", new Object[]{killer.getName(), goldPerKill});

        // persist updated progress for the killer
        try {
            PlayerProgressUtil.saveFromWallet(progressService, killer, w);
        } catch (Exception e) {
            LOG.log(java.util.logging.Level.WARNING, "Failed to persist gold after kill for {0}: {1}", new Object[]{killer.getName(), e.getMessage()});
        }
    }

    private void onBattleEnded(BattleEnded evt) {
        Player winner = evt.getWinner();
        Wallet w = walletOf(winner);
        w.add(goldForWin);
        LOG.log(java.util.logging.Level.INFO, "{0} +{1}g (victory)", new Object[]{winner.getName(), goldForWin});

        // persist both players' wallets
        try {
            for (Map.Entry<Player, Wallet> e : wallets.entrySet()) {
                Player p = e.getKey();
                Wallet wallet = e.getValue();
                PlayerProgressUtil.saveFromWallet(progressService, p, wallet);
            }
        } catch (Exception e) {
            LOG.log(java.util.logging.Level.WARNING, "Failed to persist gold after battle end: {0}", e.getMessage());
        }
    }

    public Wallet walletOf(Player p) {
        return wallets.computeIfAbsent(p, k -> new Wallet());
    }

    @Override
    public void close() throws Exception {
        Exception first = null;
        try {
            if (killUnsub != null) killUnsub.run();
        } catch (Exception e) {
            first = e;
        }
        try {
            if (endUnsub != null) endUnsub.run();
        } catch (Exception e) {
            if (first == null) first = e; else first.addSuppressed(e);
        }
        if (first != null) throw first;
    }
}
