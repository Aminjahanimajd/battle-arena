package com.amin.battlearena.domain;

import java.util.HashMap;
import java.util.Map;

public class Player {
    private String nickname;
    private int gold;
    private int level;
    private int victories;
    private int campaignProgress;
    
    // Upgrades: 0=Health, 1=Attack, 2=Defense, 3=Range, 4=Speed, 5=Precision, 6=Mana, 7=SpellPower, 8=Cooldown
    private int[] upgrades;
    
    // Inventory: Key=ItemName, Value=Count
    private Map<String, Integer> inventory;

    public Player(String nickname) {
        this.nickname = nickname;
        this.gold = 1000;
        this.level = 1;
        this.victories = 0;
        this.campaignProgress = 1;
        this.upgrades = new int[9];
        this.inventory = new HashMap<>();
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

    public void upgrade(int type) {
        if (type >= 0 && type < upgrades.length) {
            upgrades[type]++;
        }
    }

    public void addItem(String itemName) {
        inventory.put(itemName, inventory.getOrDefault(itemName, 0) + 1);
    }

    public boolean hasItem(String itemName) {
        return inventory.getOrDefault(itemName, 0) > 0;
    }

    public void useItem(String itemName) {
        if (hasItem(itemName)) {
            int count = inventory.get(itemName);
            if (count > 1) {
                inventory.put(itemName, count - 1);
            } else {
                inventory.remove(itemName);
            }
        }
    }
    
    public Map<String, Integer> getInventory() {
        return inventory;
    }

    // Setters for loading from file
    public void setGold(int gold) { this.gold = gold; }
    public void setLevel(int level) { this.level = level; }
    public void setVictories(int victories) { this.victories = victories; }
    public void setCampaignProgress(int campaignProgress) { this.campaignProgress = campaignProgress; }
    public void setUpgrades(int[] upgrades) { this.upgrades = upgrades; }
    public void setInventory(Map<String, Integer> inventory) { this.inventory = inventory; }
}
