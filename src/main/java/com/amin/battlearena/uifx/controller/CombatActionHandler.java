package com.amin.battlearena.uifx.controller;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import com.amin.battlearena.domain.actions.AttackAction;
import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.domain.model.Position;
import com.amin.battlearena.engine.core.GameEngine;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.infra.InvalidActionException;
import com.amin.battlearena.persistence.PlayerData;
import com.amin.battlearena.persistence.PlayerDataManager;
import com.amin.battlearena.players.Player;

// Handles all combat-related actions in the game
public class CombatActionHandler {
    
    // Dependencies injected from GameController
    private GameEngine engine;
    private Player human;
    private Player cpu;
    private Character selected;
    private String selectedConsumableId;
    private int selectedAbilityIndex;
    
    // State tracking maps
    private Map<Character, Integer> movesLeft;
    private Map<Character, Integer> attacksLeft;
    private Map<Position, Integer> deathFx;
    
    // Callback functions to interact with GameController
    private Consumer<String> appendLog;
    private Runnable renderBoard;
    private Runnable updateControls;
    private Runnable updateSelectedHud;
    private Function<Character, Boolean> hasAttacksLeft;
    private Function<Character, Boolean> hasMovesLeft;
    private Consumer<Character> decrementAttack;
    private BiConsumer<Position, Integer> addDeathEffect;
    private Runnable checkAndHandleGameEnd;
    private Function<Character, Integer> movesPerTurn;
    private String currentPlayerName;
    
    public CombatActionHandler() {
        // Constructor - dependencies will be injected via setters
    }
    
    // Dependency injection methods
    public void setEngine(GameEngine engine) { this.engine = engine; }
    public void setHuman(Player human) { this.human = human; }
    public void setCpu(Player cpu) { this.cpu = cpu; }
    public void setSelected(Character selected) { this.selected = selected; }
    public void setSelectedConsumableId(String selectedConsumableId) { this.selectedConsumableId = selectedConsumableId; }
    public void setSelectedAbilityIndex(int selectedAbilityIndex) { this.selectedAbilityIndex = selectedAbilityIndex; }
    public void setMovesLeft(Map<Character, Integer> movesLeft) { this.movesLeft = movesLeft; }
    public void setAttacksLeft(Map<Character, Integer> attacksLeft) { this.attacksLeft = attacksLeft; }
    public void setDeathFx(Map<Position, Integer> deathFx) { this.deathFx = deathFx; }
    public void setCurrentPlayerName(String currentPlayerName) { this.currentPlayerName = currentPlayerName; }
    
    // Callback injection methods
    public void setAppendLog(Consumer<String> appendLog) { this.appendLog = appendLog; }
    public void setRenderBoard(Runnable renderBoard) { this.renderBoard = renderBoard; }
    public void setUpdateControls(Runnable updateControls) { this.updateControls = updateControls; }
    public void setUpdateSelectedHud(Runnable updateSelectedHud) { this.updateSelectedHud = updateSelectedHud; }
    public void setHasAttacksLeft(Function<Character, Boolean> hasAttacksLeft) { this.hasAttacksLeft = hasAttacksLeft; }
    public void setHasMovesLeft(Function<Character, Boolean> hasMovesLeft) { this.hasMovesLeft = hasMovesLeft; }
    public void setDecrementAttack(Consumer<Character> decrementAttack) { this.decrementAttack = decrementAttack; }
    public void setAddDeathEffect(BiConsumer<Position, Integer> addDeathEffect) { this.addDeathEffect = addDeathEffect; }
    public void setCheckAndHandleGameEnd(Runnable checkAndHandleGameEnd) { this.checkAndHandleGameEnd = checkAndHandleGameEnd; }
    public void setMovesPerTurn(Function<Character, Integer> movesPerTurn) { this.movesPerTurn = movesPerTurn; }
    
    public void handleAttack(Character target) {
        if (engine == null) { 
            appendLog.accept("Setup the board first.\n"); 
            return; 
        }
        
        if (target == null) { 
            appendLog.accept("Click on an enemy to attack.\n"); 
            return; 
        }
        
        if (human != null && human.getTeam().contains(target)) { 
            appendLog.accept("Cannot attack own unit.\n"); 
            return; 
        }
        
        // Enforce range by Chebyshev distance
        int range = selected.getStats().getRange();
        int dx = Math.abs(target.getPosition().x() - selected.getPosition().x());
        int dy = Math.abs(target.getPosition().y() - selected.getPosition().y());
        int steps = Math.max(dx, dy);
        
        if (steps > range) { 
            appendLog.accept("Target out of range\n"); 
            return; 
        }
        
        if (!hasAttacksLeft.apply(selected)) { 
            appendLog.accept("No attacks left for this unit.\n"); 
            return; 
        }
        
        try {
            new AttackAction().execute(engine, selected, target);
            appendLog.accept(selected.getName() + " attacked " + target.getName() + "\n");
            
            if (!target.isAlive()) {
                addDeathEffect.accept(target.getPosition(), 15); // 15 ticks fade
            }
            
            renderBoard.run();
            updateControls.run();
            updateSelectedHud.run();
            decrementAttack.accept(selected);
            
            // Check if game ends after attack
            checkAndHandleGameEnd.run();
            
        } catch (InvalidActionException | DeadCharacterException ex) {
            appendLog.accept("Attack failed: " + ex.getMessage() + "\n");
        } catch (RuntimeException | Error ex) {
            appendLog.accept("Attack failed: " + ex.getMessage() + "\n");
        }
    }
    
    public void handleAbility(Character target) {
        if (engine == null) { 
            appendLog.accept("Setup the board first.\n"); 
            return; 
        }
        
        if (selected == null) { 
            appendLog.accept("Select a unit first.\n"); 
            return; 
        }
        
        if (!hasAttacksLeft.apply(selected)) { 
            appendLog.accept("No ability/attack left for this unit.\n"); 
            return; 
        }
        
        var abilities = selected.getAbilities();
        if (abilities.isEmpty()) { 
            appendLog.accept("No abilities available.\n"); 
            return; 
        }
        
        // Use selected ability or first ability as fallback
        com.amin.battlearena.domain.abilities.Ability ability;
        if (selectedAbilityIndex >= 0 && selectedAbilityIndex < abilities.size()) {
            ability = abilities.get(selectedAbilityIndex);
        } else {
            ability = abilities.get(0); // Fallback to first ability
        }
        
        // Check if ability can be used
        if (!ability.canUse(selected)) {
            if (!ability.isReady()) {
                appendLog.accept("Ability is on cooldown (" + ability.getRemainingCooldown() + " turns remaining).\n");
            } else if (!selected.canSpendMana(ability.getManaCost())) {
                appendLog.accept("Not enough mana. Need " + ability.getManaCost() + " mana.\n");
            } else {
                appendLog.accept("Cannot use " + ability.getName() + ".\n");
            }
            return;
        }
        
        try {
            // Some abilities might target self or allies, others target enemies
            if (target == null) {
                // Try targeting self for self-buff abilities
                target = selected;
            }
            
            // Check if we need an enemy target but clicked on ally/empty space
            if (target != null && human != null && human.getTeam().contains(target) && target != selected) {
                // Allow targeting allies for healing/buff abilities, but warn for offensive abilities
                String abilityName = ability.getName().toLowerCase();
                if (abilityName.contains("strike") || abilityName.contains("attack") || abilityName.contains("damage")) {
                    appendLog.accept("Cannot use offensive ability on ally.\n");
                    return;
                }
            }
            
            // Enforce ability range
            int abilityRange = selected.getAbilityRange();
            if (target == null || target.getPosition() == null || selected.getPosition() == null) {
                appendLog.accept("Invalid target or position data for range calculation\n");
                return;
            }
            
            int dx = Math.abs(target.getPosition().x() - selected.getPosition().x());
            int dy = Math.abs(target.getPosition().y() - selected.getPosition().y());
            int steps = Math.max(dx, dy);
            
            if (steps > abilityRange) { 
                appendLog.accept("Target out of ability range (" + steps + " > " + abilityRange + ")\n"); 
                return; 
            }
            
            ability.activate(selected, target, engine);
            appendLog.accept(selected.getName() + " used " + ability.getName() + " on " + target.getName() + "\n");
            
            // Add ability animation effect
            addAbilityEffect(target.getPosition());
            if (!target.isAlive()) {
                addDeathEffect.accept(target.getPosition(), 15);
            }
            
            decrementAttack.accept(selected); // Attack OR ability budget
            renderBoard.run();
            updateControls.run();
            updateSelectedHud.run();
            
            // Check if game ends after ability use
            checkAndHandleGameEnd.run();
            
        } catch (InvalidActionException | DeadCharacterException ex) {
            appendLog.accept("Ability failed: " + ex.getMessage() + "\n");
        } catch (RuntimeException ex) {
            appendLog.accept("Ability failed (runtime error): " + ex.getMessage() + "\n");
        }
    }
    
    public void handleConsumableUse(Character target) {
        if (target == null) { 
            appendLog.accept("Click a character to apply the consumable.\n"); 
            return; 
        }
        
        // Only allow applying to allies for current consumables
        if (human == null || !human.getTeam().contains(target)) {
            appendLog.accept("Consumables can be applied to your allies only.\n");
            return;
        }
        
        // Load player data to check consumables
        PlayerData playerData = PlayerDataManager.getInstance().loadPlayerData(currentPlayerName);
        if (playerData == null) { 
            appendLog.accept("Could not load player data for consumables.\n"); 
            return; 
        }
        
        int quantity = playerData.getConsumableQuantity(selectedConsumableId);
        if (quantity <= 0) { 
            appendLog.accept("No " + getConsumableName(selectedConsumableId) + " available! Visit the shop to buy more.\n"); 
            return; 
        }
        
        boolean used = applyConsumableEffect(selectedConsumableId, target);
        if (used) {
            playerData.useConsumable(selectedConsumableId);
            PlayerDataManager.getInstance().savePlayerData(playerData);
            appendLog.accept(getConsumableName(selectedConsumableId) + " remaining: " + (quantity - 1) + "\n");
            updateSelectedHud.run();
            renderBoard.run();
        }
    }
    
    private boolean applyConsumableEffect(String consumableId, Character character) {
        if ("health_potion".equals(consumableId)) {
            if (character.getStats().getHp() < character.getStats().getMaxHp()) {
                int healAmount = 50;
                int oldHp = character.getStats().getHp();
                int newHp = Math.min(character.getStats().getMaxHp(), oldHp + healAmount);
                character.getStats().setHp(newHp);
                appendLog.accept(character.getName() + " used Health Potion and recovered " + (newHp - oldHp) + " HP!\n");
                return true;
            } else {
                appendLog.accept(character.getName() + " is already at full health.\n");
                return false;
            }
            
        } else if ("mana_potion".equals(consumableId)) {
            if (character.getCurrentMana() < character.getMaxMana()) {
                int manaAmount = 30;
                int oldMana = character.getCurrentMana();
                character.restoreMana(manaAmount);
                int newMana = character.getCurrentMana();
                appendLog.accept(character.getName() + " used Mana Potion and recovered " + (newMana - oldMana) + " Mana!\n");
                return true;
            } else {
                appendLog.accept(character.getName() + " is already at full mana.\n");
                return false;
            }
            
        } else if ("strength_elixir".equals(consumableId)) {
            character.getStats().setAttack(character.getStats().getAttack() + 10);
            appendLog.accept(character.getName() + " used Strength Elixir! Attack increased by 10 for this battle!\n");
            return true;
            
        } else if ("shield_scroll".equals(consumableId)) {
            character.getStats().setDefense(character.getStats().getDefense() + 5);
            appendLog.accept(character.getName() + " used Shield Scroll! Defense increased by 5 for this battle!\n");
            return true;
            
        } else if ("haste_potion".equals(consumableId)) {
            int curMoves = movesLeft.getOrDefault(character, movesPerTurn.apply(character));
            movesLeft.put(character, curMoves + 2);
            appendLog.accept(character.getName() + " used Haste Potion! +2 moves this turn.\n");
            return true;
            
        } else if ("revival_token".equals(consumableId)) {
            if (character.isAlive()) {
                appendLog.accept(character.getName() + " is already alive. Revival Token not needed.\n");
                return false;
            }
            int quarter = Math.max(1, character.getStats().getMaxHp() / 4);
            character.getStats().setHp(quarter);
            character.isAlive();
            appendLog.accept("✨ " + character.getName() + " has been revived with " + quarter + " HP!\n");
            return true;
            
        } else {
            appendLog.accept("Unknown consumable: " + consumableId + "\n");
            return false;
        }
    }
    
    private String getConsumableName(String consumableId) {
        return switch (consumableId) {
            case "health_potion" -> "Health Potion";
            case "mana_potion" -> "Mana Potion";
            case "strength_elixir" -> "Strength Elixir";
            case "shield_scroll" -> "Shield Scroll";
            case "haste_potion" -> "Haste Potion";
            case "revival_token" -> "Revival Token";
            default -> "Unknown Consumable";
        };
    }
    
    private void addAbilityEffect(Position position) {
        // Add visual effect for ability usage (similar to death effect but different color)
        // This creates a purple sparkle effect that fades over time
        addDeathEffect.accept(position, 10); // Shorter duration than death effect
    }
}