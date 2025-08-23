package com.amin.battlearena.economy;

import java.util.Set;

public final class Shop {
    private final UpgradeCatalog catalog;

    public Shop(UpgradeCatalog catalog) { this.catalog = catalog; }

    public boolean buy(Wallet wallet, Set<String> ownedUpgradeIds, String upgradeId) {
        var u = catalog.find(upgradeId).orElse(null);
        if (u == null) return false;
        if (ownedUpgradeIds.contains(upgradeId)) return false;
        if (wallet.gold() < u.cost()) return false;
        wallet.add(-u.cost()); // spend by adding negative amount
        ownedUpgradeIds.add(upgradeId);
        return true;
    }
}
