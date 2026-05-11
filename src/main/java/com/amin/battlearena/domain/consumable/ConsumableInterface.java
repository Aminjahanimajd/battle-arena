package com.amin.battlearena.domain.consumable;

import com.amin.battlearena.domain.character.Character;

public interface ConsumableInterface {
    String getName();
    String getDescription();
    void use(Character target);
}
