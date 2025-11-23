package com.amin.battlearena.domain.ability;

import com.amin.battlearena.domain.character.GameCharacter;

public class Slash extends Ability {
    public Slash() {
        super("Slash", 0, 1, 1);
    }

    @Override
    @Override
    public void execute(GameCharacter source, GameCharacter target) {
        int damage = (int)(source.getAttack() * 1.2);
        target.takeDamage(damage);
        putOnCooldown();
    }
}
