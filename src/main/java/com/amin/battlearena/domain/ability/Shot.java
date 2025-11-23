package com.amin.battlearena.domain.ability;

import com.amin.battlearena.domain.character.GameCharacter;

public class Shot extends Ability {
    public Shot() {
        super("Power Shot", 10, 2, 3);
    }

    @Override
    @Override
    public void execute(GameCharacter source, GameCharacter target) {
        int damage = (int)(source.getAttack() * 1.5);
        target.takeDamage(damage);
        putOnCooldown();
    }
}
