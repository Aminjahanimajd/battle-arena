package com.amin.battlearena.economy;

import java.util.Objects;

import com.amin.battlearena.domain.model.Character;

// Represents an upgrade that can be purchased to improve character stats or abilities
public final class Upgrade {
    
    @FunctionalInterface
    public interface UpgradeApplier {
        void apply(Character character, int valuePerStage);
    }
    
    public enum Type {
        STAT_HP("Max HP", (character, value) -> {
            // Increase max HP and restore current HP proportionally
            int currentHp = character.getStats().getHp();
            int maxHp = character.getStats().getMaxHp();
            int newMaxHp = maxHp + value;
            character.getStats().setMaxHp(newMaxHp);
            // Restore HP proportionally
            if (maxHp > 0) {
                int newHp = (int) ((double) currentHp / maxHp * newMaxHp);
                character.getStats().setHp(newHp);
            }
        }),
        STAT_ATTACK("Attack", (character, value) -> {
            int currentAttack = character.getStats().getAttack();
            character.getStats().setAttack(currentAttack + value);
        }),
        STAT_DEFENSE("Defense", (character, value) -> {
            int currentDefense = character.getStats().getDefense();
            character.getStats().setDefense(currentDefense + value);
        }),
        STAT_SPEED("Speed", (character, value) -> {
            // Speed concept removed - convert to range upgrade instead
            int currentRange = character.getStats().getRange();
            character.getStats().setRange(currentRange + value);
        }),
        STAT_MANA("Max Mana", (character, value) -> {
            // Increase max mana by restoring additional mana
            character.restoreMana(value);
        }),
        STAT_MANA_REGEN("Mana Regeneration", (character, value) -> {
            // Note: We can't directly modify mana regen in the current system
            // This would require adding a method to Character class
        }),
        ABILITY_COOLDOWN("Ability Cooldown", (character, value) -> {
            // Note: We can't directly modify ability cooldowns in the current system
            // This would require adding methods to Ability classes
        }),
        ABILITY_MANA_COST("Ability Mana Cost", (character, value) -> {
            // Note: We can't directly modify ability mana costs in the current system
            // This would require adding methods to Ability classes
        }),
        ABILITY_DAMAGE("Ability Damage", (character, value) -> {
            // Note: We can't directly modify ability damage in the current system
            // This would require adding methods to Ability classes
        });
        
        private final String displayName;
        private final UpgradeApplier applier;
        
        Type(String displayName, UpgradeApplier applier) {
            this.displayName = displayName;
            this.applier = applier;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public void applyTo(Character character, int valuePerStage) {
            applier.apply(character, valuePerStage);
        }
    }
    
    private final String id;
    private final String name;
    private final String description;
    private final Type type;
    private final int currentStage;
    private final int maxStages;
    private final int baseValue;
    private final int valuePerStage;
    private final int baseCost;
    private final double costMultiplier;
    
    public Upgrade(String id, String name, String description, Type type, 
                   int currentStage, int maxStages, int baseValue, int valuePerStage,
                   int baseCost, double costMultiplier) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.description = Objects.requireNonNull(description, "description");
        this.type = Objects.requireNonNull(type, "type");
        this.currentStage = Math.max(0, currentStage);
        this.maxStages = Math.max(1, maxStages);
        this.baseValue = baseValue;
        this.valuePerStage = valuePerStage;
        this.baseCost = Math.max(1, baseCost);
        this.costMultiplier = Math.max(1.0, costMultiplier);
    }
    
    // Accessors
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Type getType() { return type; }
    public int getCurrentStage() { return currentStage; }
    public int getMaxStages() { return maxStages; }
    public int getBaseValue() { return baseValue; }
    public int getValuePerStage() { return valuePerStage; }
    public int getBaseCost() { return baseCost; }
    public double getCostMultiplier() { return costMultiplier; }
    
    public boolean canUpgrade() {
        return currentStage < maxStages;
    }
    
    public int getCurrentValue() {
        return baseValue + (currentStage * valuePerStage);
    }
    
    public int getNextStageValue() {
        if (!canUpgrade()) return getCurrentValue();
        return baseValue + ((currentStage + 1) * valuePerStage);
    }
    
    public int getUpgradeCost() {
        if (!canUpgrade()) return Integer.MAX_VALUE;
        return calculateCostForLevel(currentStage);
    }
    
    // Calculate cost for purchasing upgrade at a specific level
    public int calculateCostForLevel(int level) {
        return (int) (baseCost * Math.pow(costMultiplier, level));
    }
    
    // Apply this upgrade's effect to a character (Strategy Pattern)
    public void applyTo(Character character) {
        type.applyTo(character, valuePerStage);
    }
    
    public Upgrade upgrade() {
        if (!canUpgrade()) {
            throw new IllegalStateException("Cannot upgrade beyond max stage: " + maxStages);
        }
        return new Upgrade(id, name, description, type, currentStage + 1, maxStages,
                          baseValue, valuePerStage, baseCost, costMultiplier);
    }
    
    public String getUpgradeDisplay() {
        if (!canUpgrade()) {
            return String.format("%s: %d (MAX)", type.getDisplayName(), getCurrentValue());
        }
        return String.format("%s: %d → %d (Cost: %d gold)", 
                           type.getDisplayName(), getCurrentValue(), getNextStageValue(), getUpgradeCost());
    }
    
    @Override
    public String toString() {
        return String.format("Upgrade{id=%s, name=%s, type=%s, stage=%d/%d, value=%d, cost=%d}",
                           id, name, type, currentStage, maxStages, getCurrentValue(), getUpgradeCost());
    }
}
