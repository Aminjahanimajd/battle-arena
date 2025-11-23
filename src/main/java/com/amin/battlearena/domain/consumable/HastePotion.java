package com.amin.battlearena.domain.consumable;

import com.amin.battlearena.domain.character.GameCharacter;

public class HastePotion extends Consumable {
    private int speedAmount;

    public HastePotion(int speedAmount) {
        super("Haste Potion", "Increases moves by " + speedAmount);
        this.speedAmount = speedAmount;
    }

    @Override
    public void use(GameCharacter target) {
        if (target != null) {
            target.setMovesLeft(target.getMovesLeft() + speedAmount);
        }
    }
}
