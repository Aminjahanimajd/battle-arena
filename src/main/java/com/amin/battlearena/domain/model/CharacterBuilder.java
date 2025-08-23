package com.amin.battlearena.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.amin.battlearena.domain.abilities.Ability;

/**
 * Builder pattern implementation for creating Character instances.
 * Provides a fluent interface for constructing characters with different configurations.
 */
public class CharacterBuilder {
    private String name;
    @SuppressWarnings("unused")
    private Stats stats;
    private Position position;
    private final List<Ability> abilities = new ArrayList<>();
    
    public CharacterBuilder name(String name) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        return this;
    }
    
    public CharacterBuilder stats(Stats stats) {
        this.stats = Objects.requireNonNull(stats, "Stats cannot be null");
        return this;
    }
    
    public CharacterBuilder position(Position position) {
        this.position = Objects.requireNonNull(position, "Position cannot be null");
        return this;
    }
    
    public CharacterBuilder addAbility(Ability ability) {
        if (ability != null) {
            this.abilities.add(ability);
        }
        return this;
    }
    
    public CharacterBuilder abilities(List<Ability> abilities) {
        this.abilities.clear();
        if (abilities != null) {
            this.abilities.addAll(abilities);
        }
        return this;
    }
    
    /**
     * Build a Warrior with default stats if not specified.
     */
    public Warrior buildWarrior() {
        if (name == null) throw new IllegalStateException("Name must be set");
        if (position == null) throw new IllegalStateException("Position must be set");
        
        Warrior warrior = new Warrior(name, position);
        
        // Add custom abilities if specified, otherwise Warrior has default PowerStrike
        if (!abilities.isEmpty()) {
            abilities.forEach(warrior::addAbility);
        }
        
        return warrior;
    }
    
    /**
     * Build an Archer with default stats if not specified.
     */
    public Archer buildArcher() {
        return buildArcher(3); // default range
    }
    
    /**
     * Build an Archer with custom range.
     */
    public Archer buildArcher(int range) {
        if (name == null) throw new IllegalStateException("Name must be set");
        if (position == null) throw new IllegalStateException("Position must be set");
        
        Archer archer = new Archer(name, position, range);
        
        // Add custom abilities if specified, otherwise Archer has default DoubleShot
        if (!abilities.isEmpty()) {
            abilities.forEach(archer::addAbility);
        }
        
        return archer;
    }
    
    /**
     * Build a Mage with default stats if not specified.
     */
    public Mage buildMage() {
        if (name == null) throw new IllegalStateException("Name must be set");
        if (position == null) throw new IllegalStateException("Position must be set");
        
        Mage mage = new Mage(name, position);
        
        // Add custom abilities if specified, otherwise Mage has default ArcaneBurst
        if (!abilities.isEmpty()) {
            abilities.forEach(mage::addAbility);
        }
        
        return mage;
    }
    
    /**
     * Build a Knight with default stats if not specified.
     */
    public Knight buildKnight() {
        if (name == null) throw new IllegalStateException("Name must be set");
        if (position == null) throw new IllegalStateException("Position must be set");
        
        Knight knight = new Knight(name, position);
        
        // Add custom abilities if specified, otherwise Knight has default Charge
        if (!abilities.isEmpty()) {
            abilities.forEach(knight::addAbility);
        }
        
        return knight;
    }
    
    /**
     * Build a Ranger with default stats if not specified.
     */
    public Ranger buildRanger() {
        if (name == null) throw new IllegalStateException("Name must be set");
        if (position == null) throw new IllegalStateException("Position must be set");
        
        Ranger ranger = new Ranger(name, position);
        
        // Add custom abilities if specified, otherwise Ranger has default PiercingVolley
        if (!abilities.isEmpty()) {
            abilities.forEach(ranger::addAbility);
        }
        
        return ranger;
    }
    
    /**
     * Reset the builder to initial state.
     */
    public CharacterBuilder reset() {
        this.name = null;
        this.stats = null;
        this.position = null;
        this.abilities.clear();
        return this;
    }
    
    /**
     * Create a new builder instance.
     */
    public static CharacterBuilder create() {
        return new CharacterBuilder();
    }
}
