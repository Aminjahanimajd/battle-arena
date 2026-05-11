package com.amin.battlearena.domain.consumable;

import com.amin.battlearena.domain.character.Character;

public final class HastePotion extends Consumable {
    private final int speedAmount;

    public HastePotion(int speedAmount) {
        super("Haste Potion", "Increases moves by " + speedAmount);
        this.speedAmount = speedAmount;
    }

    @Override
    public void use(Character target) {
        if (target != null) {
            target.setMovesLeft(target.getMovesLeft() + speedAmount);
        }
    }
}
