package com.amin.battlearena.economy;

import com.amin.battlearena.persistence.ProgressService;
import com.amin.battlearena.players.Player;

/**
 * Utility class for loading and saving wallet data to/from progress service.
 */
public final class PlayerProgressUtil {
    
    private PlayerProgressUtil() {} // utility class
    
    /**
     * Load wallet data from progress service into the provided wallet.
     */
    public static void loadIntoWallet(ProgressService progressService, Player player, Wallet wallet) {
        try {
            var progress = progressService.load(player.getName());
            int gold = progress.wallet().gold();
            wallet.set(gold);
        } catch (Exception e) {
            // Best effort - if loading fails, wallet remains at default state
        }
    }
    
    /**
     * Save wallet data from the provided wallet to progress service.
     */
    public static void saveFromWallet(ProgressService progressService, Player player, Wallet wallet) {
        try {
            var progress = progressService.load(player.getName());
            progress.wallet().set(wallet.gold());
            progressService.save(player.getName(), progress);
        } catch (Exception e) {
            // Best effort - if saving fails, changes are lost
        }
    }
}
