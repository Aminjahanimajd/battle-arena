package com.amin.battlearena.domain.actions;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.infra.InvalidActionException;
import com.amin.battlearena.domain.model.Character;
import java.util.Objects;

/**
 * Simple defend action: grants temporary defense to the actor (one-turn buff).
 */
public final class DefendAction implements Action {

    private final int defenseAmount;

    public DefendAction() { this(6); }             // default +6 defense this turn
    public DefendAction(int defenseAmount) { this.defenseAmount = Math.max(0, defenseAmount); }

    @Override public String name() { return "Defend"; }

    @Override
    public void execute(GameEngine engine, Character actor, Character target)
            throws InvalidActionException, DeadCharacterException {
        Objects.requireNonNull(engine, "engine");
        if (actor == null) throw new InvalidActionException("Actor is null");
        if (!actor.isAlive()) throw new InvalidActionException(actor.getName() + " is dead and can't defend.");

        actor.addTemporaryDefense(defenseAmount);
        engine.log(actor.getName() + " defends and gains +" + defenseAmount + " temporary defense.");
    }
}
