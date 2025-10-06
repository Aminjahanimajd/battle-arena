package com.amin.battlearena.domain.actions;

import java.util.Objects;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.infra.InvalidActionException;

/**
 * Simple attack action that delegates to Character.attack(...) and logs result.
 */
public final class AttackAction implements Action {

    @Override
    public String name() { return "Attack"; }

    @Override
    public void execute(GameEngine engine, Character actor, Character target)
            throws InvalidActionException, DeadCharacterException {
        Objects.requireNonNull(engine, "engine");
        if (actor == null) throw new InvalidActionException("Actor is null");
        if (target == null) throw new InvalidActionException("Target is null");
        if (!actor.isAlive()) throw new InvalidActionException(actor.getName() + " is dead and can't act.");
        if (!target.isAlive()) throw new InvalidActionException("Target " + target.getName() + " is already dead.");

        // Calculate damage using improved formula for better balance
        int rawDamage = actor.getStats().getAttack() + actor.getBaseDamage();
        int effectiveDefense = target.getStats().getDefense() + target.getTemporaryDefense();
        
        // Improved damage calculation: defense reduces damage by percentage, not flat amount
        double defenseReduction = Math.min(0.6, effectiveDefense * 0.03); // Max 60% reduction, reduced multiplier
        int damage = Math.max(5, (int)(rawDamage * (1.0 - defenseReduction))); // Minimum 5 damage, increased from 3
        
        // Log the attack details
        engine.log(String.format("%s attacks %s: %d raw damage - %.1f%% defense reduction = %d damage", 
                actor.getName(), target.getName(), 
                rawDamage, defenseReduction * 100, damage));
        
        engine.applyDamage(target, damage);
        engine.log(actor.getName() + " attacks " + target.getName()
                + " (HP now " + target.getStats().getHp() + "/" + target.getStats().getMaxHp() + ")");

        // Save state after successful attack effects
        try {
            engine.getCaretaker().saveState(engine);
        } catch (Throwable t) {
            // non-fatal but log at a low level for diagnostics
            engine.log("Save state after attack failed: " + t.getMessage());
        }
    }
}
