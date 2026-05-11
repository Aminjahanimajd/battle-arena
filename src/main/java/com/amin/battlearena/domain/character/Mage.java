package com.amin.battlearena.domain.character;

import com.amin.battlearena.domain.ability.Fireball;

public final class Mage extends Character {
    public Mage(int hp, int mana, int attack, int defense, int range, int speed, boolean isPlayer) {
        super("Mage", "Mage", hp, mana, attack, defense, range, speed, isPlayer);
        addAbility(new Fireball());
    }

    @Override
    public String getIcon() {
        return "🔮";
    }
}
