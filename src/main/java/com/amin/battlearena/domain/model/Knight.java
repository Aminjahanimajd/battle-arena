package com.amin.battlearena.domain.model;

import com.amin.battlearena.domain.abilities.Charge;
import com.amin.battlearena.infra.CharacterBalanceConfig;
import com.amin.battlearena.infra.CharacterBalanceConfig.CharacterConfig;

// Tank/protector with high HP and defense
// Stats loaded from balance.json via CharacterBalanceConfig
public final class Knight extends Character {

    private static final CharacterConfig CONFIG = CharacterBalanceConfig.getInstance().getCharacterConfig("knight");

    public Knight(String name, Position position) {
        super(name, 
              new Stats(CONFIG.getHealth(), CONFIG.getAttack(), CONFIG.getDefense(), CONFIG.getRange()), 
              position, 
              CONFIG.getMaxMana(), 
              CONFIG.getManaRegen(), 
              CONFIG.getStartingMana());
        addAbility(new Charge());
    }

    @Override
    protected int calculateBaseDamage() {
        return CONFIG.getBaseDamage();
    }

    public int getMovementRange() {
        return CONFIG.getMovementRange();
    }
}
