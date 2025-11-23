package com.amin.battlearena.domain.ability;

import com.amin.battlearena.domain.character.GameCharacter;

public class Fireball extends Ability {
    public Fireball() {
        super("Fireball", 20, 3, 4);
    }

    @Override
    @Override
    public void execute(GameCharacter source, GameCharacter target) {
        int damage = source.getAttack() * 2; // Mages rely on spells, attack is spell power
        target.takeDamage(damage);
        putOnCooldown();
    }
}
