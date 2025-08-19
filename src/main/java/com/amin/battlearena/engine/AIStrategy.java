package com.amin.battlearena.engine;

import com.amin.battlearena.actions.AttackAction;
import com.amin.battlearena.model.Character;

public interface AIStrategy {
    AttackAction takeTurn(Character aiPlayer, Character humanTarget);
}
