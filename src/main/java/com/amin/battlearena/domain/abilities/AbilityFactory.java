package com.amin.battlearena.domain.abilities;

import java.util.Locale;

/**
 * Small factory to create abilities by key name (useful for config-driven loading).
 */
public final class AbilityFactory {
    private AbilityFactory() {}

    public static Ability create(String key) {
        if (key == null) throw new IllegalArgumentException("Ability key cannot be null");
        String lowerKey = key.trim().toLowerCase(Locale.ROOT);
        if ("powerstrike".equals(lowerKey)) {
            return new PowerStrike();
        } else if ("arcaneburst".equals(lowerKey)) {
            return new ArcaneBurst();
        } else if ("doubleshot".equals(lowerKey)) {
            return new DoubleShot();
        } else if ("charge".equals(lowerKey)) {
            return new Charge();
        } else if ("masterstrike".equals(lowerKey)) {
            return new MasterStrike();
        } else if ("piercingvolley".equals(lowerKey)) {
            return new PiercingVolley();
        } else if ("evasion".equals(lowerKey)) {
            return new Evasion();
        } else {
            throw new IllegalArgumentException("Unknown ability key: " + key);
        }
    }
}
