package com.amin.battlearena.actions;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.model.Character;
import com.amin.battlearena.exceptions.InvalidActionException;
import com.amin.battlearena.exceptions.DeadCharacterException;

// Abstraction via interface; polymorphic execute().
public interface Action {
    String name();
    void execute(GameEngine game, Character actor, Character target)
        throws InvalidActionException, DeadCharacterException;
}