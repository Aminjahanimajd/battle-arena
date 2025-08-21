package com.amin.battlearena.economy;

public final class Wallet {
    private int gold;
    public int gold() { return gold; }
    public void add(int amount) { gold += Math.max(0, amount); }
    public boolean spend(int amount) {
        if (amount <= gold) { gold -= amount; return true; }
        return false;
    }
}
