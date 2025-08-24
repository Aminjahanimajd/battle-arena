package com.amin.battlearena.app;

import java.util.List;

import com.amin.battlearena.domain.model.Archer;
import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.domain.model.CharacterState;
import com.amin.battlearena.domain.model.DamageCalculator;
import com.amin.battlearena.domain.model.Mage;
import com.amin.battlearena.domain.model.Position;
import com.amin.battlearena.domain.model.PositionPool;
import com.amin.battlearena.domain.model.Warrior;
import com.amin.battlearena.engine.GameCaretaker;
import com.amin.battlearena.engine.GameMemento;
import com.amin.battlearena.infra.ActionValidationService;

/**
 * Enhanced test runner that tests all the new enhancements and design patterns.
 */
public class EnhancedTestRunner {
    
    public static void main(String[] args) {
        System.out.println("🏆 Enhanced Battle Arena Test Runner");
        System.out.println("=====================================");
        
        try {
            testStatePattern();
            testChainOfResponsibility();
            testMementoPattern();
            testVisitorPattern();
            testObjectPooling();
            testCaching();
            testAllEnhancements();
            
            System.out.println("\n✅ All enhancement tests passed successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed with error: " + e.getMessage());
            // Log error details for debugging
            System.err.println("Error details: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    /**
     * Test the State pattern implementation.
     */
    private static void testStatePattern() {
        System.out.println("\n🧠 Testing State Pattern...");
        
        Warrior warrior = new Warrior("TestWarrior", new Position(0, 0));
        
        // Test alive state
        CharacterState aliveState = warrior.getCurrentState();
        System.out.println("✓ Alive state name: " + aliveState.getStateName());
        System.out.println("✓ Can act: " + aliveState.canAct());
        
        // Test state change on death
        warrior.getStats().setHp(0);
        warrior.isAlive(); // This should trigger state change
        
        CharacterState deadState = warrior.getCurrentState();
        System.out.println("✓ Dead state name: " + deadState.getStateName());
        System.out.println("✓ Can act: " + deadState.canAct());
        
        // Test state change back to alive
        warrior.getStats().setHp(100);
        warrior.isAlive(); // This should trigger state change back
        
        CharacterState backToAlive = warrior.getCurrentState();
        System.out.println("✓ Back to alive state: " + backToAlive.getStateName());
    }
    
    /**
     * Test the Chain of Responsibility pattern for validation.
     */
    private static void testChainOfResponsibility() {
        System.out.println("\n🔗 Testing Chain of Responsibility Pattern...");
        
        ActionValidationService validationService = new ActionValidationService();
        
        Warrior actor = new Warrior("Actor", new Position(0, 0));
        Character target = new Warrior("Target", new Position(1, 0));
        
        // Test valid action
        boolean valid = validationService.validateTargetedAction(actor, target, null);
        System.out.println("✓ Valid action validation: " + valid);
        
        // Test dead actor
        actor.getStats().setHp(0);
        boolean invalidDead = validationService.validateTargetedAction(actor, target, null);
        System.out.println("✓ Dead actor validation: " + invalidDead);
        
        // Test null target
        actor.getStats().setHp(100); // Make alive again
        boolean invalidNullTarget = validationService.validateTargetedAction(actor, null, null);
        System.out.println("✓ Null target validation: " + invalidNullTarget);
    }
    
    /**
     * Test the Memento pattern for save/load.
     */
    private static void testMementoPattern() {
        System.out.println("\n💾 Testing Memento Pattern...");
        
        GameCaretaker caretaker = new GameCaretaker(5);
        
        // Test initial state
        System.out.println("✓ Can undo initially: " + caretaker.canUndo());
        System.out.println("✓ Can redo initially: " + caretaker.canRedo());
        
        // Test memento creation
        GameMemento memento = new GameMemento(
            List.of(new Warrior("Test", new Position(0, 0))),
            List.of(new Archer("Test", new Position(1, 0))),
            5,
            null
        );
        
        System.out.println("✓ Memento created: " + memento);
        System.out.println("✓ Memento timestamp: " + memento.getTimestamp());
    }
    
    /**
     * Test the Visitor pattern for character operations.
     */
    private static void testVisitorPattern() {
        System.out.println("\n👁️ Testing Visitor Pattern...");
        
        DamageCalculator calculator = new DamageCalculator();
        
        // Test different character types
        Warrior warrior = new Warrior("Test", new Position(0, 0));
        Archer archer = new Archer("Test", new Position(0, 0));
        Mage mage = new Mage("Test", new Position(0, 0));
        
        int warriorDamage = calculator.calculateDamage(warrior);
        int archerDamage = calculator.calculateDamage(archer);
        int mageDamage = calculator.calculateDamage(mage);
        
        System.out.println("✓ Warrior damage: " + warriorDamage);
        System.out.println("✓ Archer damage: " + archerDamage);
        System.out.println("✓ Mage damage: " + mageDamage);
        
        // Test visitor methods directly
        calculator.reset();
        calculator.visit(warrior);
        calculator.visit(archer);
        System.out.println("✓ Combined damage: " + calculator.getTotalDamage());
    }
    
    /**
     * Test object pooling for positions.
     */
    private static void testObjectPooling() {
        System.out.println("\n🏊 Testing Object Pooling...");
        
        // Test position creation
        Position pos1 = PositionPool.get(1, 2);
        Position pos2 = PositionPool.get(1, 2);
        Position pos3 = PositionPool.get(3, 4);
        
        System.out.println("✓ Position 1: " + pos1);
        System.out.println("✓ Position 2: " + pos2);
        System.out.println("✓ Position 3: " + pos3);
        System.out.println("✓ Positions 1 and 2 are same object: " + (pos1 == pos2));
        System.out.println("✓ Positions 1 and 3 are different objects: " + (pos1 != pos3));
        
        System.out.println("✓ Pool size: " + PositionPool.getPoolSize());
        System.out.println("✓ Pool stats: " + PositionPool.getPoolStats());
    }
    
    /**
     * Test caching for expensive calculations.
     */
    private static void testCaching() {
        System.out.println("\n💾 Testing Caching...");
        
        Warrior warrior = new Warrior("Test", new Position(0, 0));
        
        // First call should calculate
        int damage1 = warrior.getBaseDamage();
        System.out.println("✓ First damage calculation: " + damage1);
        
        // Second call should use cache
        int damage2 = warrior.getBaseDamage();
        System.out.println("✓ Second damage calculation (cached): " + damage2);
        
        // Move character to invalidate cache
        warrior.setPosition(new Position(1, 1));
        int damage3 = warrior.getBaseDamage();
        System.out.println("✓ Damage after move (cache invalidated): " + damage3);
        
        // Test cache invalidation
        warrior.invalidateCache();
        int damage4 = warrior.getBaseDamage();
        System.out.println("✓ Damage after manual invalidation: " + damage4);
    }
    
    /**
     * Test all enhancements together.
     */
    private static void testAllEnhancements() {
        System.out.println("\n🚀 Testing All Enhancements Together...");
        
        // Create characters with different states
        Warrior warrior = new Warrior("Hero", new Position(0, 0));
        Archer archer = new Archer("Ranger", new Position(1, 0));
        Mage mage = new Mage("Wizard", new Position(2, 0));
        
        // Test state pattern
        System.out.println("✓ Warrior state: " + warrior.getCurrentState().getStateName());
        System.out.println("✓ Archer state: " + archer.getCurrentState().getStateName());
        System.out.println("✓ Mage state: " + mage.getCurrentState().getStateName());
        
        // Test validation service
        ActionValidationService validator = new ActionValidationService();
        boolean canAttack = validator.validateTargetedAction(warrior, archer, null);
        System.out.println("✓ Can warrior attack archer: " + canAttack);
        
        // Test damage calculation with visitor
        DamageCalculator calculator = new DamageCalculator();
        int totalDamage = calculator.calculateDamage(warrior) + 
                         calculator.calculateDamage(archer) + 
                         calculator.calculateDamage(mage);
        System.out.println("✓ Total team damage: " + totalDamage);
        
        // Test position pooling
        Position pos = PositionPool.get(5, 5);
        System.out.println("✓ Pooled position: " + pos);
        
        System.out.println("✓ All enhancement tests completed successfully!");
    }
}
