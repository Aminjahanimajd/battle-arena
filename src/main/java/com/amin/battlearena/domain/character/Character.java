package com.amin.battlearena.domain.character;

import java.util.ArrayList;
import java.util.List;

import com.amin.battlearena.domain.Tile;
import com.amin.battlearena.domain.ability.AbilityInterface;

public abstract class Character implements CharacterInterface {
    private final String name;
    private final String type;
    private final int maxHp;
    private int currentHp;
    private final int maxMana;
    private int currentMana;
    private final int attack;
    private final int defense;
    private final int range;
    private final int speed;
    private int movesLeft;
    private int attacksLeft;
    private final boolean isPlayerTeam;
    private final List<AbilityInterface> abilities;
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public int getMaxHp() {
        return maxHp;
    }

    @Override
    public int getCurrentHp() {
        return currentHp;
    }

    @Override
    public int getMaxMana() {
        return maxMana;
    }

    @Override
    public int getCurrentMana() {
        return currentMana;
    }

    @Override
    public int getAttack() {
        return attack;
    }

    @Override
    public int getDefense() {
        return defense;
    }

    @Override
    public int getRange() {
        return range;
    }

    @Override
    public int getSpeed() {
        return speed;
    }

    @Override
    public boolean isPlayerTeam() {
        return isPlayerTeam;
    }

    @Override
    public List<AbilityInterface> getAbilities() {
        return abilities;
    }

    @Override
    public Tile getPosition() {
        return position;
    }

    @Override
    public void setPosition(Tile position) {
        this.position = position;
    }

    @Override
    public int getMovesLeft() {
        return movesLeft;
    }

    @Override
    public void setMovesLeft(int movesLeft) {
        this.movesLeft = movesLeft;
    }

    @Override
    public void useMove() {
        if (movesLeft > 0) {
            movesLeft--;
        }
    }

    @Override
    public int getAttacksLeft() {
        return attacksLeft;
    }

    @Override
    public void setAttacksLeft(int attacksLeft) {
        this.attacksLeft = attacksLeft;
    }

    @Override
    public void useAttack() {
        if (attacksLeft > 0) {
            attacksLeft--;
        }
    }

    @Override
    public void resetTurn() {
        this.movesLeft = speed;
        this.attacksLeft = 1;
        for (AbilityInterface a : abilities) {
            a.reduceCooldown();
        }
    }

    @Override
    public void takeDamage(int amount) {
        int actualDamage = Math.max(1, amount - defense);
        this.currentHp -= actualDamage;
        if (this.currentHp < 0) {
            this.currentHp = 0;
        }
    }

    @Override
    public void heal(int amount) {
        this.currentHp += amount;
        if (this.currentHp > maxHp) {
            this.currentHp = maxHp;
        }
    }

    @Override
    public void restoreMana(int amount) {
        this.currentMana += amount;
        if (this.currentMana > maxMana) {
            this.currentMana = maxMana;
        }
    }

    @Override
    public void spendMana(int amount) {
        this.currentMana -= amount;
    }

    @Override
    public boolean isAlive() {
        return currentHp > 0;
    }

    @Override
    public void addAbility(AbilityInterface ability) {
        abilities.add(ability);
    }

    @Override
    public abstract String getIcon();
}
