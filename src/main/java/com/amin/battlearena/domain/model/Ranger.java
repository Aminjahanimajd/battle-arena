package com.amin.battlearena.domain.model;

import com.amin.battlearena.domain.abilities.PiercingVolley;
import com.amin.battlearena.infra.CharacterBalanceConfig;
import com.amin.battlearena.infra.CharacterBalanceConfig.CharacterConfig;

// Elite ranged attacker with superior mobility and damage
// Stats loaded from balance.json via CharacterBalanceConfig
public final class Ranger extends Character {

    private static final CharacterConfig CONFIG = CharacterBalanceConfig.getInstance().getCharacterConfig("ranger");

    public Ranger(String name, Position position) {
        super(name, 
              new Stats(CONFIG.getHealth(), CONFIG.getAttack(), CONFIG.getDefense(), CONFIG.getRange()), 
              position, 
              CONFIG.getMaxMana(), 
              CONFIG.getManaRegen(), 
              CONFIG.getStartingMana());
        addAbility(new PiercingVolley());
    }

    @Override
    protected int calculateBaseDamage() {
        return CONFIG.getBaseDamage();
    }

    public int getMovementRange() {
        return CONFIG.getMovementRange();
    }

    public boolean inRangeOf(Character target) {
        if (target == null) return false;
        return getPosition().distanceTo(target.getPosition()) <= getStats().getRange();
    }
}
