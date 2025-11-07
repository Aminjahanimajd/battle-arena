package com.amin.battlearena.economy;

import java.util.List;

import com.amin.battlearena.domain.model.Character;

// Shop system for purchasing upgrades and applying them to characters
public final class Shop {
    private final UpgradeCatalog catalog;

    public Shop(UpgradeCatalog catalog) { 
        this.catalog = catalog; 
    }

    public UpgradeCatalog getCatalog() { return catalog; }

    public boolean purchaseUpgrade(Wallet wallet, Character character, String upgradeId) {
        Upgrade upgrade = UpgradeCatalog.findUpgradeById(upgradeId);
        if (upgrade == null) return false;
        
        if (wallet.getGold() < upgrade.getUpgradeCost()) return false;
        
        // Spend the gold
        wallet.add(-upgrade.getUpgradeCost());
        
        // Apply the upgrade to the character
        applyUpgradeToCharacter(character, upgrade);
        
        return true;
    }
    
    public boolean purchaseUpgrade(com.amin.battlearena.persistence.PlayerData playerData, String upgradeId) {
        Upgrade upgrade = UpgradeCatalog.findUpgradeById(upgradeId);
        if (upgrade == null) return false;
        
        int currentLevel = playerData.getPurchasedUpgradeLevel(upgradeId);
        if (currentLevel >= upgrade.getMaxStages()) return false; // Max level reached
        
        // Calculate cost for next level
        int cost = upgrade.calculateCostForLevel(currentLevel);
        
        if (playerData.getGold() < cost) return false;
        
        // Spend the gold
        playerData.addGold(-cost);
        
        // Update upgrade level
        playerData.setPurchasedUpgradeLevel(upgradeId, currentLevel + 1);
        
        return true;
    }

    private void applyUpgradeToCharacter(Character character, Upgrade upgrade) {
        upgrade.applyTo(character);
    }
    
    public void applyPurchasedUpgrades(Character character, com.amin.battlearena.persistence.PlayerData playerData) {
        String characterType = character.getClass().getSimpleName();
        List<Upgrade> characterUpgrades = UpgradeCatalog.getUpgradesForCharacter(characterType);
        
        for (Upgrade upgrade : characterUpgrades) {
            int level = playerData.getPurchasedUpgradeLevel(upgrade.getId());
            for (int i = 0; i < level; i++) {
                applyUpgradeToCharacter(character, upgrade);
            }
        }
        
        // Apply global upgrades
        List<Upgrade> globalUpgrades = UpgradeCatalog.getUpgradesForCharacter("Global");
        for (Upgrade upgrade : globalUpgrades) {
            int level = playerData.getPurchasedUpgradeLevel(upgrade.getId());
            for (int i = 0; i < level; i++) {
                applyUpgradeToCharacter(character, upgrade);
            }
        }
    }

    public int getUpgradeCost(String upgradeId) {
        Upgrade upgrade = UpgradeCatalog.findUpgradeById(upgradeId);
        return upgrade != null ? upgrade.getUpgradeCost() : Integer.MAX_VALUE;
    }
}
