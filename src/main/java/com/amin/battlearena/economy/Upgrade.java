package com.amin.battlearena.economy;

import java.util.Objects;

// Represents an upgrade that can be purchased to improve character stats or abilities
public final class Upgrade {
    
    public enum Type {
        STAT_HP("Max HP"),
        STAT_ATTACK("Attack"),
        STAT_DEFENSE("Defense"),
        STAT_SPEED("Speed"),
        STAT_MANA("Max Mana"),
        STAT_MANA_REGEN("Mana Regeneration"),
        ABILITY_COOLDOWN("Ability Cooldown"),
        ABILITY_MANA_COST("Ability Mana Cost"),
        ABILITY_DAMAGE("Ability Damage");
        
        private final String displayName;
        
        Type(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
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
        return (int) (baseCost * Math.pow(costMultiplier, currentStage));
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
