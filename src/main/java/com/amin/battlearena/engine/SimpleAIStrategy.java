package com.amin.battlearena.engine;

import com.amin.battlearena.actions.AttackAction;
import com.amin.battlearena.model.Character;

public class SimpleAIStrategy implements AIStrategy {

    @Override
    public AttackAction takeTurn(Character ai, Character player) {
        try {
            return new AttackAction();
        } catch (Exception e) {
            System.out.println("AI failed to attack: " + e.getMessage());
        }
        return null;
    }
}
