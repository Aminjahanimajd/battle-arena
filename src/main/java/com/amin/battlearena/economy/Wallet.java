package com.amin.battlearena.economy;

public final class Wallet {
    private int gold = 0;
    public Wallet() {}
    public int getGold() { return gold; }
    public void add(int amount) { if (amount > 0) gold += amount; }
    public void set(int amount) { this.gold = Math.max(0, amount); }
}
