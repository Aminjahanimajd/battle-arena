package com.amin.battlearena.domain.model;

import com.amin.battlearena.domain.abilities.ArcaneBurst;
import com.amin.battlearena.infra.CharacterBalanceConfig;
import com.amin.battlearena.infra.CharacterBalanceConfig.CharacterConfig;

// Magical damage dealer with powerful spells
// Stats loaded from balance.json via CharacterBalanceConfig
public final class Mage extends Character {

    private static final CharacterConfig CONFIG = CharacterBalanceConfig.getInstance().getCharacterConfig("mage");

    public Mage(String name, Position position) {
        super(name, 
              new Stats(CONFIG.getHealth(), CONFIG.getAttack(), CONFIG.getDefense(), CONFIG.getRange()), 
              position, 
              CONFIG.getMaxMana(), 
              CONFIG.getManaRegen(), 
              CONFIG.getStartingMana());
        addAbility(new ArcaneBurst());
    }

    @Override
    protected int calculateBaseDamage() {
        return CONFIG.getBaseDamage();
    }

    public int getMovementRange() {
        return CONFIG.getMovementRange();
    }
}
