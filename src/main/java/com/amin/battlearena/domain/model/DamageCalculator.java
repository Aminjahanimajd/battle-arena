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
        totalDamage += warrior.getStats().getAttack() + 2; // warrior damage bonus
        totalDefense += warrior.getStats().getDefense() + 1; // warrior defense bonus
    }
    
    @Override
    public void visit(Archer archer) {
        totalDamage += archer.getStats().getAttack() + 1; // archer damage bonus
        totalDefense += archer.getStats().getDefense() + 0; // archer defense bonus
    }
    
    @Override
    public void visit(Mage mage) {
        totalDamage += mage.getStats().getAttack() + 3; // mage damage bonus
        totalDefense += mage.getStats().getDefense() + (-1); // mage defense bonus
    }
    
    @Override
    public void visit(Knight knight) {
        totalDamage += knight.getStats().getAttack() + 1; // knight damage bonus
        totalDefense += knight.getStats().getDefense() + 2; // knight defense bonus
    }
    
    @Override
    public void visit(Ranger ranger) {
        totalDamage += ranger.getStats().getAttack() + 2; // ranger damage bonus
        totalDefense += ranger.getStats().getDefense() + 0; // ranger defense bonus
    }
    
    @Override
    public void visit(Master master) {
        totalDamage += master.getStats().getAttack() + 4; // master damage bonus
        totalDefense += master.getStats().getDefense() + 1; // master defense bonus
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
