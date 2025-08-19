package com.amin.battlearena.engine;

import com.amin.battlearena.model.Character;

public interface AIStrategy {
    void takeTurn(Character aiPlayer, Character humanTarget);
}
