package com.amin.battlearena.domain.character;

import com.amin.battlearena.domain.Tile;
import com.amin.battlearena.domain.ability.Ability;
import java.util.ArrayList;
import java.util.List;

public abstract class GameCharacter {
    private String name;
    private String type; // Warrior, Archer, etc.
    private int maxHp;
    private int currentHp;
    private int maxMana;
    private int currentMana;
    private int attack;
    private int defense;
    private int range;
    private int speed;
    private int movesLeft;
    private int attacksLeft;
    private boolean isPlayerTeam;
    private List<Ability> abilities;
    private Tile position;

    public Character(String name, String type, int maxHp, int maxMana, int attack, int defense, int range, int speed, boolean isPlayerTeam) {
        this.name = name;
        this.type = type;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.maxMana = maxMana;
        this.currentMana = maxMana;
        this.attack = attack;
        this.defense = defense;
        this.range = range;
        this.speed = speed;
        this.isPlayerTeam = isPlayerTeam;
        this.abilities = new ArrayList<>();
        this.movesLeft = speed;
        this.attacksLeft = 1;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public int getMaxHp() { return maxHp; }
    public int getCurrentHp() { return currentHp; }
    public int getMaxMana() { return maxMana; }
    public int getCurrentMana() { return currentMana; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getRange() { return range; }
    public int getSpeed() { return speed; }
    public boolean isPlayerTeam() { return isPlayerTeam; }
    public List<Ability> getAbilities() { return abilities; }
    public Tile getPosition() { return position; }
    public void setPosition(Tile position) { this.position = position; }
    
    public int getMovesLeft() { return movesLeft; }
    public void setMovesLeft(int movesLeft) { this.movesLeft = movesLeft; }
    public void useMove() { if (movesLeft > 0) movesLeft--; }
    
    public int getAttacksLeft() { return attacksLeft; }
    public void setAttacksLeft(int attacksLeft) { this.attacksLeft = attacksLeft; }
    public void useAttack() { if (attacksLeft > 0) attacksLeft--; }

    public void resetTurn() {
        this.movesLeft = speed;
        this.attacksLeft = 1;
        for (Ability a : abilities) {
            a.reduceCooldown();
        }
    }

    public void takeDamage(int amount) {
        int actualDamage = Math.max(1, amount - defense);
        this.currentHp -= actualDamage;
        if (this.currentHp < 0) this.currentHp = 0;
    }

    public void heal(int amount) {
        this.currentHp += amount;
        if (this.currentHp > maxHp) this.currentHp = maxHp;
    }

    public void restoreMana(int amount) {
        this.currentMana += amount;
        if (this.currentMana > maxMana) this.currentMana = maxMana;
    }

    public void spendMana(int amount) {
        this.currentMana -= amount;
    }

    public boolean isAlive() {
        return currentHp > 0;
    }

    public void addAbility(Ability ability) {
        abilities.add(ability);
    }

    public abstract String getIcon();
}
