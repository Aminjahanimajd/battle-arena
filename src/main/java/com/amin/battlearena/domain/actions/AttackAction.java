package com.amin.battlearena.domain.actions;

import java.util.Objects;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.infra.InvalidActionException;
import com.amin.battlearena.domain.model.Character;

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

        // Calculate damage using actor's attack + baseDamage minus target's defense
        int rawDamage = actor.getStats().getAttack() + actor.getBaseDamage();
        int effectiveDefense = target.getStats().getDefense() + target.getTemporaryDefense();
        int damage = Math.max(0, rawDamage - effectiveDefense);
        
        engine.applyDamage(target, damage, actor);

        engine.log(actor.getName() + " attacks " + target.getName()
                + " (HP now " + target.getStats().getHp() + "/" + target.getStats().getMaxHp() + ")");

        // engine may subscribe to dead events or inspect target state after call
    }
}
