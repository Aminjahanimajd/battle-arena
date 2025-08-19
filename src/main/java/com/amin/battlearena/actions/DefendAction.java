package com.amin.battlearena.actions;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.exceptions.DeadCharacterException;
import com.amin.battlearena.exceptions.InvalidActionException;
import com.amin.battlearena.model.Character;

public final class DefendAction implements Action {

    @Override
    public String name() {
        return "Defend";
    }

    @Override
    public void execute(GameEngine game, Character actor, Character target)
            throws InvalidActionException, DeadCharacterException {

        actor.addTemporaryDefense(5); // Boost defense by 5 points
        // Log action
        game.log(actor.getName() + " defends and increases defense temporarily!");
    }
}
