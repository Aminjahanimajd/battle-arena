package com.amin.battlearena.engine;

import com.amin.battlearena.actions.Action;
import com.amin.battlearena.actions.AttackAction;
import com.amin.battlearena.model.Character;

public class SimpleAIStrategy implements AIStrategy {

    private final Action attackAction = new AttackAction();

    @Override
    public void takeTurn(Character aiPlayer, Character humanTarget) {
        try {
            attackAction.execute(aiPlayer, humanTarget);
            System.out.println(aiPlayer.getName() + " attacks " + humanTarget.getName() + "!");
        } catch (Exception e) {
            System.out.println("AI failed to act: " + e.getMessage());
        }
    }
}
