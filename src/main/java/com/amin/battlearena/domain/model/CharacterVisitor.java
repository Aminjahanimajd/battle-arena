package com.amin.battlearena.domain.model;

/**
 * Interface for visitors that can perform operations on different character types.
 * Implements the Visitor pattern for character-specific operations.
 */
public interface CharacterVisitor {
    
    /**
     * Visit a Warrior character.
     * @param warrior the warrior to visit
     */
    void visit(Warrior warrior);
    
    /**
     * Visit an Archer character.
     * @param archer the archer to visit
     */
    void visit(Archer archer);
    
    /**
     * Visit a Mage character.
     * @param mage the mage to visit
     */
    void visit(Mage mage);
    
    /**
     * Visit a Knight character.
     * @param knight the knight to visit
     */
    void visit(Knight knight);
    
    /**
     * Visit a Ranger character.
     * @param ranger the ranger to visit
     */
    void visit(Ranger ranger);
    
    /**
     * Visit a Master character.
     * @param master the master to visit
     */
    void visit(Master master);
}
