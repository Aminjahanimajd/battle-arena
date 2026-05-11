package com.amin.battlearena.domain.character;

import com.amin.battlearena.domain.ability.Slash;

public final class Warrior extends Character {
    public Warrior(int hp, int mana, int attack, int defense, int range, int speed, boolean isPlayer) {
        super("Warrior", "Warrior", hp, mana, attack, defense, range, speed, isPlayer);
        addAbility(new Slash());
    }

    @Override
    public String getIcon() {
        return "🛡";
    }
}
