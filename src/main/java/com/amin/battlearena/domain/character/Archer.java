package com.amin.battlearena.domain.character;

import com.amin.battlearena.domain.ability.Shot;

public final class Archer extends Character {
    public Archer(int hp, int mana, int attack, int defense, int range, int speed, boolean isPlayer) {
        super("Archer", "Archer", hp, mana, attack, defense, range, speed, isPlayer);
        addAbility(new Shot());
    }

    @Override
    public String getIcon() {
        return "🏹";
    }
}
