package com.amin.battlearena.domain.model;

/**
 * Visitor that calculates damage for different character types.
 * Implements the Visitor pattern to provide character-specific damage calculations.
 */
public class DamageCalculator implements CharacterVisitor {
    
    private int totalDamage = 0;
    private int totalDefense = 0;
    
    @Override
    public void visit(Warrior warrior) {
        totalDamage += warrior.getStats().getAttack() + 2; // Warrior bonus
        totalDefense += warrior.getStats().getDefense() + 1; // Warrior defense bonus
    }
    
    @Override
    public void visit(Archer archer) {
        totalDamage += archer.getStats().getAttack() + 1; // Archer bonus
        totalDefense += archer.getStats().getDefense(); // No defense bonus
    }
    
    @Override
    public void visit(Mage mage) {
        totalDamage += mage.getStats().getAttack() + 3; // Mage bonus
        totalDefense += mage.getStats().getDefense() - 1; // Mage defense penalty
    }
    
    @Override
    public void visit(Knight knight) {
        totalDamage += knight.getStats().getAttack() + 1; // Knight bonus
        totalDefense += knight.getStats().getDefense() + 2; // Knight defense bonus
    }
    
    @Override
    public void visit(Ranger ranger) {
        totalDamage += ranger.getStats().getAttack() + 2; // Ranger bonus
        totalDefense += ranger.getStats().getDefense(); // No defense bonus
    }
    
    @Override
    public void visit(Master master) {
        totalDamage += master.getStats().getAttack() + 4; // Master bonus
        totalDefense += master.getStats().getDefense() + 1; // Master defense bonus
    }
    
    /**
     * Get the calculated total damage.
     * @return the total damage
     */
    public int getTotalDamage() {
        return totalDamage;
    }
    
    /**
     * Get the calculated total defense.
     * @return the total defense
     */
    public int getTotalDefense() {
        return totalDefense;
    }
    
    /**
     * Reset the calculator for a new calculation.
     */
    public void reset() {
        totalDamage = 0;
        totalDefense = 0;
    }
    
    /**
     * Calculate damage for a specific character.
     * @param character the character to calculate damage for
     * @return the calculated damage
     */
    public int calculateDamage(Character character) {
        reset();
        
        // Use the visitor pattern to calculate damage
        if (character instanceof Warrior warrior) {
            visit(warrior);
        } else if (character instanceof Archer archer) {
            visit(archer);
        } else if (character instanceof Mage mage) {
            visit(mage);
        } else if (character instanceof Knight knight) {
            visit(knight);
        } else if (character instanceof Ranger ranger) {
            visit(ranger);
        } else if (character instanceof Master master) {
            visit(master);
        }
        
        return totalDamage;
    }
}
