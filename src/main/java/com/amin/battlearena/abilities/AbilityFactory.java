package com.amin.battlearena.abilities;

import java.util.Locale;

/**
 * Small factory to create abilities by key name (useful for config-driven loading).
 */
public final class AbilityFactory {
    private AbilityFactory() {}

    public static Ability create(String key) {
        if (key == null) throw new IllegalArgumentException("Ability key cannot be null");
        switch (key.trim().toLowerCase(Locale.ROOT)) {
            case "powerstrike":    return new PowerStrike();
            case "arcaneburst":    return new ArcaneBurst();
            case "doubleshot":     return new DoubleShot();
            case "charge":         return new Charge();
            case "masterstrike":   return new MasterStrike();
            case "piercingvolley": return new PiercingVolley();
            case "evasion":        return new Evasion();
            default: throw new IllegalArgumentException("Unknown ability key: " + key);
        }
    }
}
