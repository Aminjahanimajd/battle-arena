package com.amin.battlearena.domain.ability;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class AbilityFactory {
    private static final Map<String, Supplier<Ability>> ABILITIES = new HashMap<>();
    
    static {
        ABILITIES.put("Slash", Slash::new);
        ABILITIES.put("Fireball", Fireball::new);
        ABILITIES.put("Shot", Shot::new);
    }
    
    private AbilityFactory() {}
    
    public static Ability create(String abilityName) {
        Supplier<Ability> supplier = ABILITIES.get(abilityName);
        if (supplier != null) {
            return supplier.get();
        }
        throw new IllegalArgumentException("Unknown ability: " + abilityName);
    }
}
