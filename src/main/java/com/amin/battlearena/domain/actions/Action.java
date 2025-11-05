package com.amin.battlearena.domain.actions;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.infra.InvalidActionException;

// Domain action interface (attack, defend, move, skill)
public interface Action {
    String name();

    void execute(GameEngine engine, Character actor, Character target)
            throws InvalidActionException, DeadCharacterException;
}
