package com.amin.battlearena.economy;

import com.amin.battlearena.model.Character;
import com.amin.battlearena.model.Stats;

/**
 * Simple stat upgrades that work with the existing Stats API.
 *
 * Note: Stats in this codebase does not expose range accessors. Archer stores
 * range on the Archer class itself (private final int range). Because of that,
 * this upgrade silently ignores range upgrades. If you want range upgrades to
 * actually take effect, see the suggestions at the bottom of the file.
 */
public final class StatUpgrade {

    private final int hpPlus;
    private final int atkPlus;
    private final int defPlus;
    private final int rangePlus; // kept for constructor compatibility (ignored if Stats doesn't support it)

    /**
     * Construct a StatUpgrade.
     *
     * @param hpPlus    amount to add to current HP (clamped so HP >= 1)
     * @param atkPlus   amount to add to attack (clamped so attack >= 0)
     * @param defPlus   amount to add to defense (clamped so defense >= 0)
     * @param rangePlus amount to add to range (ignored unless Stats/Character exposes range setters)
     */
    public StatUpgrade(int hpPlus, int atkPlus, int defPlus, int rangePlus) {
        this.hpPlus = hpPlus;
        this.atkPlus = atkPlus;
        this.defPlus = defPlus;
        this.rangePlus = rangePlus;
    }

    /**
     * Apply the upgrade to a character.
     *
     * This uses the Stats API which provides setHp/setAttack/setDefense etc.
     * Range is not applied here unless the project's Stats/Character classes
     * are extended to expose setters for range (see notes below).
     */
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
