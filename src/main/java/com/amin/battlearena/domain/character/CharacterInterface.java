package com.amin.battlearena.domain.character;

import java.util.List;

import com.amin.battlearena.domain.Tile;
import com.amin.battlearena.domain.ability.AbilityInterface;

public interface CharacterInterface {
    String getName();
    String getType();
    int getMaxHp();
    int getCurrentHp();
    int getMaxMana();
    int getCurrentMana();
    int getAttack();
    int getDefense();
    int getRange();
    int getSpeed();
    boolean isPlayerTeam();
    List<AbilityInterface> getAbilities();
    Tile getPosition();
    void setPosition(Tile position);
    int getMovesLeft();
    void setMovesLeft(int movesLeft);
    void useMove();
    int getAttacksLeft();
    void setAttacksLeft(int attacksLeft);
    void useAttack();
    void resetTurn();
    void takeDamage(int amount);
    void heal(int amount);
    void restoreMana(int amount);
    void spendMana(int amount);
    boolean isAlive();
    void addAbility(AbilityInterface ability);
    String getIcon();
}
