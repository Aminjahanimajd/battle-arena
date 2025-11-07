package com.amin.battlearena.engine.ai;

import java.util.Comparator;
import java.util.List;

import com.amin.battlearena.domain.abilities.Ability;
import com.amin.battlearena.domain.actions.AttackAction;
import com.amin.battlearena.domain.model.Archer;
import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.domain.model.Position;
import com.amin.battlearena.engine.core.GameEngine;
import com.amin.battlearena.players.Player;

// Enhanced AI strategy that properly handles abilities and range checking
public final class SimpleAIStrategy implements AIStrategy {

    @Override
    public void takeTurn(GameEngine engine, Player aiPlayer) throws Exception {
        List<Character> myAlive = aiPlayer.aliveTeam();
        if (myAlive.isEmpty()) return;
        Player opponent = engine.getOpponentOf(aiPlayer);
        List<Character> enemyAlive = opponent.aliveTeam();
        if (enemyAlive.isEmpty()) return;

        Character actor = myAlive.get(0); // simple pick
        
        // First, try to use abilities if available and in range
        if (tryUseAbility(engine, actor, enemyAlive)) {
            actor.endTurnHousekeeping();
            return;
        }
        
        // try to find an enemy in range for basic attack
        Character inRange = enemyAlive.stream()
                .filter(e -> isInAttackRange(actor, e))
                .min(Comparator.comparingInt(e -> e.getStats().getHp()))
                .orElse(null);

        if (inRange != null) {
            new AttackAction().execute(engine, actor, inRange);
            actor.endTurnHousekeeping();
            return;
        }

        // move towards nearest enemy
        Character nearest = enemyAlive.stream()
                .min(Comparator.comparingInt(e -> actor.getPosition().distanceTo(e.getPosition())))
                .orElse(null);
        if (nearest == null) return;
        
        Position next = actor.getPosition().stepTowards(nearest.getPosition());
        try {
            engine.move(actor, next);
            actor.endTurnHousekeeping();
        } catch (Exception e) {
            // Movement failed, skip
            engine.log("AI move failed: " + e.getMessage());
        }
    }
    
    private boolean tryUseAbility(GameEngine engine, Character actor, List<Character> enemies) {
        List<Ability> abilities = actor.getAbilities();
        if (abilities.isEmpty()) return false;
        
        for (Ability ability : abilities) {
            if (!ability.canUse(actor)) continue; // Check mana and cooldown
            
            // Find a target in range for this ability
            Character target = enemies.stream()
                    .filter(e -> isInAbilityRange(actor, e, ability))
                    .min(Comparator.comparingInt(e -> e.getStats().getHp()))
                    .orElse(null);
                    
            if (target != null) {
                try {
                    ability.activate(actor, target, engine);
                    return true;
                } catch (com.amin.battlearena.infra.InvalidActionException | com.amin.battlearena.infra.DeadCharacterException e) {
                    // Ability failed, try next one
                }
            }
        }
        return false;
    }
    
    private boolean isInAttackRange(Character attacker, Character target) {
        if (attacker == null || target == null) return false;
        if (attacker instanceof Archer archer) {
            return archer.inRangeOf(target);
        }
        // Use character's attack range from stats
        return attacker.getPosition() != null && target.getPosition() != null && 
               attacker.getPosition().distanceTo(target.getPosition()) <= attacker.getStats().getRange();
    }
    
    private boolean isInAbilityRange(Character user, Character target, Ability ability) {
        int distance = user.getPosition().distanceTo(target.getPosition());
        
        // Use ability's specific range if available
        if (ability instanceof com.amin.battlearena.domain.abilities.AbstractAbility abstractAbility) {
            return distance <= abstractAbility.getRange();
        }
        
        // Fallback: Use character's ability range
        return distance <= user.getAbilityRange();
    }
}
