package com.amin.battlearena.domain.model;

import java.util.List;
import java.util.function.Supplier;

import com.amin.battlearena.domain.abilities.Ability;

/**
 * Example implementation of lazy loading for abilities.
 * This demonstrates how abilities could be loaded on-demand for performance optimization.
 */
public abstract class LazyCharacter extends Character {
    
    private final Supplier<List<Ability>> abilitiesSupplier;
    private List<Ability> abilities;
    
    protected LazyCharacter(String name, Stats stats, Position pos, int maxMana, 
                           int manaRegenPerTurn, int startingMana, 
                           Supplier<List<Ability>> abilitiesSupplier) {
        super(name, stats, pos, maxMana, manaRegenPerTurn, startingMana);
        this.abilitiesSupplier = abilitiesSupplier;
    }
    
    @Override
    public List<Ability> getAbilities() {
        if (abilities == null) {
            abilities = abilitiesSupplier.get();
        }
        return List.copyOf(abilities);
    }
    
    /**
     * Force reload of abilities from supplier.
     */
    public void reloadAbilities() {
        abilities = null;
    }
}
