package com.amin.battlearena.persistence;

import com.amin.battlearena.economy.Wallet;
import com.amin.battlearena.players.Player;
import com.amin.battlearena.progression.PlayerProgress;

public final class PlayerProgressUtil {
    private PlayerProgressUtil() {}

    public static void loadIntoWallet(ProgressService svc, Player player, Wallet wallet) {
        if (svc == null || player == null || wallet == null) return;
        PlayerProgress p = svc.load(player.getName());
        wallet.set(p.wallet().getGold());
    }

    public static void saveFromWallet(ProgressService svc, Player player, Wallet wallet) {
        if (svc == null || player == null || wallet == null) return;
        PlayerProgress p = svc.load(player.getName()); // keep unlocked levels existing
        p.wallet().set(wallet.getGold());
        svc.save(player.getName(), p);
    }
}
