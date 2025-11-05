package com.amin.battlearena.domain.model;

// Mana pool value object
public final class Mana {
    private int current;
    private final int max;
    private final int regenPerTurn;

    public Mana(int max, int regenPerTurn, int starting) {
        this.max = Math.max(0, max);
        this.regenPerTurn = Math.max(0, regenPerTurn);
        this.current = Math.min(Math.max(0, starting), this.max);
    }

    public int getCurrent() { return current; }
    public int getMax() { return max; }
    public int getRegenPerTurn() { return regenPerTurn; }

    public boolean isEnough(int cost) { return current >= Math.max(0, cost); }

    public boolean useMana(int amount) {
        int a = Math.max(0, amount);
        if (current < a) return false;
        current -= a;
        return true;
    }

    public void regen() {
        current = Math.min(max, current + regenPerTurn);
    }

    public void restore(int amount) {
        if (amount <= 0) return;
        current = Math.min(max, current + amount);
    }
}