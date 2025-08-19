package com.amin.battlearena.actions;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.model.Character;

public class DefendAction implements Action {

    @Override
    public void execute(GameEngine engine, Character actor, Character target) {
        actor.addTemporaryDefense(5);
        System.out.println(actor.getName() + " is defending and gains 5 defense!");
    }

    @Override
    public String name() {
        return "Defend";
    }
}
