package com.amin.battlearena.domain.model;

// Visitor for character-specific damage calculations
public class DamageCalculator implements CharacterVisitor {
    
    private int totalDamage = 0;
    private int totalDefense = 0;
    
    @Override
    public void visit(Warrior warrior) {
        totalDamage += warrior.getStats().getAttack() + 2;
        totalDefense += warrior.getStats().getDefense() + 1;
    }
    
    @Override
    public void visit(Archer archer) {
        totalDamage += archer.getStats().getAttack() + 1;
        totalDefense += archer.getStats().getDefense() + 0;
    }
    
    @Override
    public void visit(Mage mage) {
        totalDamage += mage.getStats().getAttack() + 3;
        totalDefense += mage.getStats().getDefense() + (-1);
    }
    
    @Override
    public void visit(Knight knight) {
        totalDamage += knight.getStats().getAttack() + 1;
        totalDefense += knight.getStats().getDefense() + 2;
    }
    
    @Override
    public void visit(Ranger ranger) {
        totalDamage += ranger.getStats().getAttack() + 2;
        totalDefense += ranger.getStats().getDefense() + 0;
    }
    
    @Override
    public void visit(Master master) {
        totalDamage += master.getStats().getAttack() + 4;
        totalDefense += master.getStats().getDefense() + 1;
    }
    
    public int getTotalDamage() {
        return totalDamage;
    }
    
    public int getTotalDefense() {
        return totalDefense;
    }
    
    public void reset() {
        totalDamage = 0;
        totalDefense = 0;
    }
    
    public int calculateDamage(Character character) {
        reset();
        
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
