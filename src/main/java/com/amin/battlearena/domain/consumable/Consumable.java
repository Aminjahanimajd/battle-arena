package com.amin.battlearena.domain.consumable;

import com.amin.battlearena.domain.character.GameCharacter;

public abstract class Consumable {
    private final String name;
    private final String description;

    public Consumable(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }

    public abstract void use(GameCharacter target);
}
