package com.amin.battlearena.domain.model;

// Visitor pattern for character-specific operations
public interface CharacterVisitor {
    
    void visit(Warrior warrior);
    
    void visit(Archer archer);
    
    void visit(Mage mage);
    
    void visit(Knight knight);
    
    void visit(Ranger ranger);
    
    void visit(Master master);
}
