package com.amin.battlearena.domain.account;

import java.util.Map;

import com.amin.battlearena.domain.Inventory;

public final class Player {
    private String nickname;
    private int gold;
    private int level;
    private int victories;
    private int campaignProgress;
    
    // Upgrades: 0=Health, 1=Attack, 2=Defense, 3=Range, 4=Speed, 5=Precision, 6=Mana, 7=SpellPower, 8=Cooldown
    private int[] upgrades;
    
    // Inventory
    private Inventory inventory;

    public Player(String nickname) {
        this.nickname = nickname;
        this.gold = 1000;
        this.level = 1;
        this.victories = 0;
        this.campaignProgress = 1;
        this.upgrades = new int[9];
        this.inventory = new Inventory();
    }

    public String getNickname() {
        return nickname;
    }

    public int getGold() {
        return gold;
    }

    public void addGold(int amount) {
        this.gold += amount;
    }

    public void spendGold(int amount) {
        if (this.gold >= amount) {
            this.gold -= amount;
        }
    }

    public int getLevel() {
        return level;
    }

    public void levelUp() {
        this.level++;
    }

    public int getVictories() {
        return victories;
    }

    public void addVictory() {
        this.victories++;
    }

    public int getCampaignProgress() {
        return campaignProgress;
    }

    public void unlockNextLevel() {
        this.campaignProgress++;
    }

    public int getUpgradeLevel(int type) {
        if (type >= 0 && type < upgrades.length) {
            return upgrades[type];
        }
        return 0;
    }

    public void incrementUpgrade(int type) {
        if (type >= 0 && type < upgrades.length) {
            upgrades[type]++;
        }
    }

    public void upgrade(int type) {
        if (type >= 0 && type < upgrades.length) {
            upgrades[type]++;
        }
    }

    public void addItem(String itemName) {
        inventory.addItem(itemName);
    }

    public boolean hasItem(String itemName) {
        return inventory.hasItem(itemName);
    }

    public void useItem(String itemName) {
        inventory.removeItem(itemName);
    }
    
    public Map<String, Integer> getInventory() {
        return inventory.getAllItems();
    }

    // Setters for loading from file
    public void setGold(int gold) { this.gold = gold; }
    public void setLevel(int level) { this.level = level; }
    public void setVictories(int victories) { this.victories = victories; }
    public void setCampaignProgress(int campaignProgress) { this.campaignProgress = campaignProgress; }
    public void setUpgrades(int[] upgrades) { this.upgrades = upgrades; }
    public void setInventory(Map<String, Integer> inventoryItems) { 
        this.inventory.setItems(inventoryItems); 
    }
}
