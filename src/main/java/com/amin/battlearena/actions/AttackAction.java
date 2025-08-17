package com.amin.battlearena.actions;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.model.Character;
import com.amin.battlearena.exceptions.InvalidActionException;
import com.amin.battlearena.exceptions.DeadCharacterException;

public final class AttackAction implements Action {
    @Override public String name() { return "Attack"; }

    @Override
    public void execute(GameEngine game, Character actor, Character target)
            throws InvalidActionException, DeadCharacterException {
        actor.attack(target);
        game.log(actor.getName() + " attacks " + target.getName()
                + " (HP now " + target.getStats().getHp() + ")");
    }
}