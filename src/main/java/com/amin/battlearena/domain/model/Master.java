package com.amin.battlearena.domain.model;

import com.amin.battlearena.domain.abilities.Evasion;
import com.amin.battlearena.domain.abilities.MasterStrike;
import com.amin.battlearena.infra.CharacterBalanceConfig;
import com.amin.battlearena.infra.CharacterBalanceConfig.CharacterConfig;

// Boss-level character with multiple abilities and high stats
// Stats loaded from balance.json via CharacterBalanceConfig
public final class Master extends Character {

    private static final CharacterConfig CONFIG = CharacterBalanceConfig.getInstance().getCharacterConfig("master");

    public Master(String name, Position position) {
        super(name, 
              new Stats(CONFIG.getHealth(), CONFIG.getAttack(), CONFIG.getDefense(), CONFIG.getRange()), 
              position, 
              CONFIG.getMaxMana(), 
              CONFIG.getManaRegen(), 
              CONFIG.getStartingMana());
        addAbility(new MasterStrike());
        addAbility(new Evasion());
    }

    @Override
    protected int calculateBaseDamage() {
        return CONFIG.getBaseDamage();
    }

    public int getMovementRange() {
        return CONFIG.getMovementRange();
    }
}
