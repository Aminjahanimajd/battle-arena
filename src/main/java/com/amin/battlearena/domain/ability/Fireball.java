package com.amin.battlearena.domain.ability;

import com.amin.battlearena.domain.character.Character;

public class Fireball extends Ability {
    public Fireball() {
        super("Fireball", 20, 3, 4);
    }

    @Override
    public void execute(Character source, Character target) {
        int damage = source.getAttack() * 2; // Mages rely on spells, attack is spell power
        target.takeDamage(damage);
        putOnCooldown();
    }
}
