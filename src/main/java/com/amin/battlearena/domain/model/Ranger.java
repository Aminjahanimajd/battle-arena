package com.amin.battlearena.domain.model;

import com.amin.battlearena.domain.abilities.Evasion;
import com.amin.battlearena.domain.abilities.PiercingVolley;

/**
 * Boss-level large archer: extends Archer but with boosted stats and two abilities.
 */
public final class Ranger extends Archer {

    public Ranger(String name, Position position) {
        super(name, position, 4); // longer range
        // boost base stats strongly for boss behavior
        getStats().modifyMaxHp(60);   // base 80 -> 140
        getStats().modifyAttack(7);   // base 15 -> 22
        getStats().modifyDefense(5);  // base 5 -> 10
        // abilities
        addAbility(new PiercingVolley());
        addAbility(new Evasion());
    }

    @Override
    public int baseDamage() {
        return 4;
    }
}
