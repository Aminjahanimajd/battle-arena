package com.amin.battlearena.domain.consumable;

import com.amin.battlearena.domain.character.Character;

public abstract class Consumable implements ConsumableInterface {
    private final String name;
    private final String description;

    public Consumable(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }

    public abstract void use(Character target);
}
