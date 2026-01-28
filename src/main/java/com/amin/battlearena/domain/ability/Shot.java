package com.amin.battlearena.domain.ability;

import com.amin.battlearena.domain.character.Character;

public class Shot extends Ability {
    public Shot() {
        super("Power Shot", 10, 2, 3);
    }

    @Override
    public void execute(Character source, Character target) {
        int damage = (int)(source.getAttack() * 1.5);
        target.takeDamage(damage);
        putOnCooldown();
    }
}
