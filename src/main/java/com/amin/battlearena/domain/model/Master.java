package com.amin.battlearena.domain.model;

import com.amin.battlearena.domain.abilities.Evasion;
import com.amin.battlearena.domain.abilities.MasterStrike;

// Boss-level character with multiple abilities and high stats
public final class Master extends Character {

    public Master(String name, Position position) {
        super(name, new Stats(200, 20, 12, 4), position, 80, 10, 40);
        addAbility(new MasterStrike());
        addAbility(new Evasion());
    }

    @Override
    protected int calculateBaseDamage() {
        return 8; // Increased from 5 for better combat balance - masters are powerful
    }

    public int getMovementRange() {
        return 2;
    }
}
