package com.amin.battlearena.domain.ability;

import com.amin.battlearena.domain.character.Character;

public class Slash extends Ability {
    public Slash() {
        super("Slash", 0, 1, 1);
    }

    @Override
    public void execute(Character source, Character target) {
        int damage = (int)(source.getAttack() * 1.2);
        target.takeDamage(damage);
        putOnCooldown();
    }
}
