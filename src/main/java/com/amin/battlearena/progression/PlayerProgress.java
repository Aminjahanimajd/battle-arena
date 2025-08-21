package com.amin.battlearena.progression;

import java.util.HashSet;
import java.util.Set;

import com.amin.battlearena.economy.UpgradeCatalog;
import com.amin.battlearena.economy.Wallet;
import com.amin.battlearena.model.Character;

public final class PlayerProgress {
    private final Wallet wallet = new Wallet();
    private final Set<String> unlockedLevels = new HashSet<>();
    private final Set<String> ownedUpgrades = new HashSet<>();

    public Wallet wallet() { return wallet; }
    public boolean isUnlocked(String levelId) { return unlockedLevels.contains(levelId); }
    public void unlock(String levelId) { unlockedLevels.add(levelId); }
    public Set<String> ownedUpgrades() { return ownedUpgrades; }

    public void applyOwnedUpgrades(Character c, UpgradeCatalog catalog) {
        for (String id : ownedUpgrades) catalog.find(id).ifPresent(up -> up.apply(c));
    }
}
