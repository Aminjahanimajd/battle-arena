package com.amin.battlearena.economy;

import java.util.List;

import com.amin.battlearena.domain.model.Character;

/**
 * Shop system for purchasing upgrades and applying them to characters.
 */
public final class Shop {
    private final UpgradeCatalog catalog;

    public Shop(UpgradeCatalog catalog) { 
        this.catalog = catalog; 
    }

    public UpgradeCatalog getCatalog() { return catalog; }

    /**
     * Get all available upgrades for a specific character type.
     */
    public List<Upgrade> getUpgradesForCharacter(String characterType) {
        return UpgradeCatalog.getUpgradesForCharacter(characterType);
    }

    /**
     * Get all available upgrades.
     */
    public List<Upgrade> getAllUpgrades() {
        return UpgradeCatalog.getAllUpgrades();
    }

    /**
     * Purchase and apply an upgrade to a character.
     */
    public boolean purchaseUpgrade(Wallet wallet, Character character, String upgradeId) {
        Upgrade upgrade = UpgradeCatalog.findUpgradeById(upgradeId);
        if (upgrade == null) return false;
        
        if (wallet.gold() < upgrade.getUpgradeCost()) return false;
        
        // Spend the gold
        wallet.add(-upgrade.getUpgradeCost());
        
        // Apply the upgrade to the character
        applyUpgradeToCharacter(character, upgrade);
        
        return true;
    }
    
    /**
     * Purchase upgrade using PlayerData system.
     */
    public boolean purchaseUpgrade(com.amin.battlearena.persistence.PlayerData playerData, String upgradeId) {
        Upgrade upgrade = UpgradeCatalog.findUpgradeById(upgradeId);
        if (upgrade == null) return false;
        
        int currentLevel = playerData.getPurchasedUpgradeLevel(upgradeId);
        if (currentLevel >= upgrade.getMaxStages()) return false; // Max level reached
        
        // Calculate cost for next level
        int cost = calculateUpgradeCost(upgrade, currentLevel + 1);
        
        if (playerData.getGold() < cost) return false;
        
        // Spend the gold
        playerData.addGold(-cost);
        
        // Update upgrade level
        playerData.setPurchasedUpgradeLevel(upgradeId, currentLevel + 1);
        
        return true;
    }
    
    private int calculateUpgradeCost(Upgrade upgrade, int level) {
        return (int) (upgrade.getBaseCost() * Math.pow(upgrade.getCostMultiplier(), level - 1));
    }

    /**
     * Apply an upgrade to a character's stats or abilities.
     */
    private void applyUpgradeToCharacter(Character character, Upgrade upgrade) {
        if (upgrade.getType() == Upgrade.Type.STAT_HP) {
            // Increase max HP and restore current HP proportionally
            int currentHp = character.getStats().getHp();
            int maxHp = character.getStats().getMaxHp();
            int newMaxHp = maxHp + upgrade.getValuePerStage();
            character.getStats().setMaxHp(newMaxHp);
            // Restore HP proportionally
            if (maxHp > 0) {
                int newHp = (int) ((double) currentHp / maxHp * newMaxHp);
                character.getStats().setHp(newHp);
            }
        } else if (upgrade.getType() == Upgrade.Type.STAT_ATTACK) {
            int currentAttack = character.getStats().getAttack();
            character.getStats().setAttack(currentAttack + upgrade.getValuePerStage());
        } else if (upgrade.getType() == Upgrade.Type.STAT_DEFENSE) {
            int currentDefense = character.getStats().getDefense();
            character.getStats().setDefense(currentDefense + upgrade.getValuePerStage());
        } else if (upgrade.getType() == Upgrade.Type.STAT_SPEED) {
            // Speed concept removed - convert to range upgrade instead
            int currentRange = character.getStats().getRange();
            character.getStats().setRange(currentRange + upgrade.getValuePerStage());
        } else if (upgrade.getType() == Upgrade.Type.STAT_MANA) {
            // Increase max mana by restoring additional mana
            character.restoreMana(upgrade.getValuePerStage());
        } else if (upgrade.getType() == Upgrade.Type.STAT_MANA_REGEN) {
            // Note: We can't directly modify mana regen in the current system
            // This would require adding a method to Character class
        } else if (upgrade.getType() == Upgrade.Type.ABILITY_COOLDOWN) {
            // Note: We can't directly modify ability cooldowns in the current system
            // This would require adding methods to Ability classes
        } else if (upgrade.getType() == Upgrade.Type.ABILITY_MANA_COST) {
            // Note: We can't directly modify ability mana costs in the current system
            // This would require adding methods to Ability classes
        } else if (upgrade.getType() == Upgrade.Type.ABILITY_DAMAGE) {
            // Note: We can't directly modify ability damage in the current system
            // This would require adding methods to Ability classes
        }
    }
    
    /**
     * Apply all purchased upgrades to a character based on player data.
     */
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

    /**
     * Get the cost of an upgrade.
     */
    public int getUpgradeCost(String upgradeId) {
        Upgrade upgrade = UpgradeCatalog.findUpgradeById(upgradeId);
        return upgrade != null ? upgrade.getUpgradeCost() : Integer.MAX_VALUE;
    }
}
