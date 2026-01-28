package com.amin.battlearena.domain.ability;

import com.amin.battlearena.domain.character.Character;

public abstract class Ability {
    private final String name;
    private final int manaCost;
    private final int cooldown;
    private final int range;
    private int currentCooldown;

    public Ability(String name, int manaCost, int cooldown, int range) {
        this.name = name;
        this.manaCost = manaCost;
        this.cooldown = cooldown;
        this.range = range;
        this.currentCooldown = 0;
    }

    public String getName() { return name; }
    public int getManaCost() { return manaCost; }
    public int getCooldown() { return cooldown; }
    public int getRange() { return range; }
    public int getCurrentCooldown() { return currentCooldown; }

    public boolean isReady() { return currentCooldown == 0; }
    
    public void reduceCooldown() {
        if (currentCooldown > 0) currentCooldown--;
    }
    
    public void putOnCooldown() {
        currentCooldown = cooldown;
    }

    public abstract void execute(Character source, Character target);
}
