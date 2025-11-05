package com.amin.battlearena.economy;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.domain.model.Stats;

// Simple stat upgrades that work with the existing Stats API
public final class StatUpgrade {

    private final int hpPlus;
    private final int atkPlus;
    private final int defPlus;
    @SuppressWarnings("unused")
    private final int rangePlus; // kept for constructor compatibility (ignored if Stats doesn't support it)

    public StatUpgrade(int hpPlus, int atkPlus, int defPlus, int rangePlus) {
        this.hpPlus = hpPlus;
        this.atkPlus = atkPlus;
        this.defPlus = defPlus;
        this.rangePlus = rangePlus;
    }

    public void apply(Character c) {
        Stats s = c.getStats();
        if (s == null) return;

        // increase current HP; ensure at least 1
        s.setHp(Math.max(1, s.getHp() + hpPlus));

        // attack & defense (never negative)
        s.setAttack(Math.max(0, s.getAttack() + atkPlus));
        s.setDefense(Math.max(0, s.getDefense() + defPlus));
    }
}
