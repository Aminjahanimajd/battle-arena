package com.amin.battlearena.domain.shop;

import com.amin.battlearena.domain.account.Player;

public final class Shop {
    private final UpgradeService upgradeService;
    private final ConsumableShop consumableShop;
    
    public Shop() {
        this.upgradeService = new UpgradeService();
        this.consumableShop = new ConsumableShop();
    }
    
    public boolean purchaseUpgrade(Player player, int upgradeType) {
        int cost = upgradeService.getUpgradeCost(upgradeType);
        if (player.getGold() >= cost) {
            player.setGold(player.getGold() - cost);
            player.incrementUpgrade(upgradeType);
            return true;
        }
        return false;
    }
    
    public boolean purchaseConsumable(Player player, String consumableName) {
        int price = consumableShop.getPrice(consumableName);
        if (player.getGold() >= price) {
            player.setGold(player.getGold() - price);
            player.addItem(consumableName);
            return true;
        }
        return false;
    }
    
    public int getUpgradeCost(int upgradeType) {
        return upgradeService.getUpgradeCost(upgradeType);
    }
    
    public int getConsumablePrice(String consumableName) {
        return consumableShop.getPrice(consumableName);
    }
    
    public String getUpgradeName(int upgradeType) {
        return upgradeService.getUpgradeName(upgradeType);
    }
}
