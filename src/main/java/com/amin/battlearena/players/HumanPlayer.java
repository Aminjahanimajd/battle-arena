package com.amin.battlearena.players;

import java.util.List;
import java.util.Scanner;

import com.amin.battlearena.domain.abilities.Ability;
import com.amin.battlearena.domain.actions.AttackAction;
import com.amin.battlearena.domain.actions.DefendAction;
import com.amin.battlearena.domain.model.Archer;
import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.domain.model.Master;
import com.amin.battlearena.domain.model.Position;
import com.amin.battlearena.domain.model.Ranger;
import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.infra.InvalidActionException;

/**
 * Simple console-driven human player. Prompts user to choose an acting character and an action.
 * This is intentionally simple and can be replaced by a GUI controller later.
 */
public final class HumanPlayer extends Player {

    private final Scanner scanner;

    public HumanPlayer(String name) {
        super(name);
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void takeTurn(GameEngine engine) {
        List<Character> alive = aliveTeam();
        if (alive.isEmpty()) {
            engine.log(getName() + " has no alive characters.");
            return;
        }

        System.out.println("\n[" + getName() + "] Choose a character to act:");
        for (int i = 0; i < alive.size(); i++) {
            var c = alive.get(i);
            System.out.printf("%d) %s %s%n", i + 1, c.getName(), c);
        }
        System.out.print("Choice (number): ");
        
        try {
            if (!scanner.hasNextInt()) {
                engine.log("[Error] Please enter a valid number");
                scanner.nextLine(); // clear invalid input
                return;
            }
            
            int choice = scanner.nextInt();
            if (choice < 1 || choice > alive.size()) {
                engine.log("[Error] Please enter a number between 1 and " + alive.size());
                return;
            }
            
            int idx = choice - 1;
            Character actor = alive.get(idx);

            // Show available actions including abilities
            System.out.println("Actions: 1) Attack  2) Defend  3) Move  4) Use Ability");
            System.out.print("Choice: ");
            
            if (!scanner.hasNextInt()) {
                engine.log("[Error] Please enter a valid number for action choice");
                scanner.nextLine(); // clear invalid input
                return;
            }
            
            int actionChoice = scanner.nextInt();
            if (actionChoice < 1 || actionChoice > 4) {
                engine.log("[Error] Please enter a number between 1 and 4 for action choice");
                return;
            }

            try {
                switch (actionChoice) {
                    case 1 -> { // Attack: choose target
                        var enemyAlive = engine.getOpponentOf(this).aliveTeam();
                        if (enemyAlive.isEmpty()) {
                            engine.log("No enemies to attack.");
                            return;
                        }
                        System.out.println("Choose target:");
                        for (int i = 0; i < enemyAlive.size(); i++) {
                            var t = enemyAlive.get(i);
                            System.out.printf("%d) %s %s%n", i + 1, t.getName(), t);
                        }
                        System.out.print("Target choice: ");
                        
                        if (!scanner.hasNextInt()) {
                            engine.log("[Error] Please enter a valid number for target choice");
                            scanner.nextLine(); // clear invalid input
                            return;
                        }
                        
                        int targetChoice = scanner.nextInt();
                        if (targetChoice < 1 || targetChoice > enemyAlive.size()) {
                            engine.log("[Error] Please enter a number between 1 and " + enemyAlive.size());
                            return;
                        }
                        
                        int tIdx = targetChoice - 1;
                        new AttackAction().execute(engine, actor, enemyAlive.get(tIdx));
                    }
                    case 2 -> {
                        new DefendAction().execute(engine, actor, null);
                    }
                    case 3 -> {
                        // Movement with validation
                        int maxMove = getMovementRange(actor);
                        System.out.println(actor.getName() + " can move up to " + maxMove + " spaces");
                        System.out.print("Enter new X: "); 
                        if (!scanner.hasNextInt()) {
                            engine.log("[Error] Please enter a valid number for X coordinate");
                            scanner.nextLine(); // clear invalid input
                            return;
                        }
                        int nx = scanner.nextInt();
                        
                        System.out.print("Enter new Y: "); 
                        if (!scanner.hasNextInt()) {
                            engine.log("[Error] Please enter a valid number for Y coordinate");
                            scanner.nextLine(); // clear invalid input
                            return;
                        }
                        int ny = scanner.nextInt();
                        
                        Position newPos = new Position(nx, ny);
                        Position currentPos = actor.getPosition();
                        
                        // Validate movement distance
                        int distance = currentPos.distanceTo(newPos);
                        if (distance > maxMove) {
                            engine.log("[Error] " + actor.getName() + " can only move " + maxMove + " spaces, not " + distance);
                            return;
                        }
                        
                        engine.move(actor, newPos);
                    }
                    case 4 -> {
                        // Use ability
                        List<Ability> abilities = actor.getAbilities();
                        if (abilities.isEmpty()) {
                            engine.log(actor.getName() + " has no abilities.");
                            return;
                        }
                        
                        System.out.println("Available abilities:");
                        for (int i = 0; i < abilities.size(); i++) {
                            Ability ability = abilities.get(i);
                            String status = ability.canUse(actor) ? "READY" : 
                                          !ability.isReady() ? "COOLDOWN(" + ability.getRemainingCooldown() + ")" :
                                          "NO MANA(" + ability.getManaCost() + ")";
                            System.out.printf("%d) %s - %s [%s]%n", i + 1, ability.getName(), ability.getDescription(), status);
                        }
                        
                        System.out.print("Choose ability: ");
                        if (!scanner.hasNextInt()) {
                            engine.log("[Error] Please enter a valid number for ability choice");
                            scanner.nextLine(); // clear invalid input
                            return;
                        }
                        
                        int abilityChoice = scanner.nextInt();
                        if (abilityChoice < 1 || abilityChoice > abilities.size()) {
                            engine.log("[Error] Please enter a number between 1 and " + abilities.size());
                            return;
                        }
                        
                        Ability selectedAbility = abilities.get(abilityChoice - 1);
                        
                        if (!selectedAbility.canUse(actor)) {
                            if (!selectedAbility.isReady()) {
                                engine.log("[Error] " + selectedAbility.getName() + " is on cooldown");
                            } else {
                                engine.log("[Error] Not enough mana for " + selectedAbility.getName());
                            }
                            return;
                        }
                        
                        // If ability needs a target, prompt for one
                        if (selectedAbility.getName().contains("Attack") || selectedAbility.getName().contains("Strike") || 
                            selectedAbility.getName().contains("Burst") || selectedAbility.getName().contains("Volley")) {
                            
                            var enemyAlive = engine.getOpponentOf(this).aliveTeam();
                            if (enemyAlive.isEmpty()) {
                                engine.log("No enemies to target.");
                                return;
                            }
                            
                            System.out.println("Choose target:");
                            for (int i = 0; i < enemyAlive.size(); i++) {
                                var t = enemyAlive.get(i);
                                System.out.printf("%d) %s %s%n", i + 1, t.getName(), t);
                            }
                            System.out.print("Target choice: ");
                            
                            if (!scanner.hasNextInt()) {
                                engine.log("[Error] Please enter a valid number for target choice");
                                scanner.nextLine(); // clear invalid input
                                return;
                            }
                            
                            int targetChoice = scanner.nextInt();
                            if (targetChoice < 1 || targetChoice > enemyAlive.size()) {
                                engine.log("[Error] Please enter a number between 1 and " + enemyAlive.size());
                                return;
                            }
                            
                            int tIdx = targetChoice - 1;
                            selectedAbility.activate(actor, enemyAlive.get(tIdx), engine);
                        } else {
                            // Self-targeting ability
                            selectedAbility.activate(actor, null, engine);
                        }
                    }
                }
            } catch (InvalidActionException | DeadCharacterException ex) {
                engine.log("[Error] " + ex.getMessage());
            } finally {
                // reduce cooldowns/clear temp buffs for this actor
                actor.endTurnHousekeeping();
            }
        } catch (Exception e) {
            engine.log("[Error] Input error: " + e.getMessage());
            // Clear scanner buffer on error
            scanner.nextLine();
        }
    }

    /**
     * Get the movement range for a character based on their class.
     */
    private int getMovementRange(Character character) {
        if (character instanceof Archer) return 2;
        if (character instanceof Ranger) return 3;
        if (character instanceof Master) return 2;
        // Warrior, Knight, Mage all move 1 space
        return 1;
    }

    /**
     * Clean up resources when the player is no longer needed
     */
    public void cleanup() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
