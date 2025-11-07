package com.amin.battlearena.domain.model;

import com.amin.battlearena.domain.abilities.DoubleShot;
import com.amin.battlearena.infra.CharacterBalanceConfig;
import com.amin.battlearena.infra.CharacterBalanceConfig.CharacterConfig;

// Ranged attacker with good mobility
// Stats loaded from balance.json via CharacterBalanceConfig
public final class Archer extends Character {

    private static final CharacterConfig CONFIG = CharacterBalanceConfig.getInstance().getCharacterConfig("archer");

    public Archer(String name, Position position) {
        super(name, 
              new Stats(CONFIG.getHealth(), CONFIG.getAttack(), CONFIG.getDefense(), CONFIG.getRange()), 
              position, 
              CONFIG.getMaxMana(), 
              CONFIG.getManaRegen(), 
              CONFIG.getStartingMana());
        addAbility(new DoubleShot());
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
