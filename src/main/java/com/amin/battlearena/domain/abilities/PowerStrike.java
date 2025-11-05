package com.amin.battlearena.domain.abilities;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.infra.InvalidActionException;

// Warrior ability: powerful melee strike with bonus damage
public final class PowerStrike extends AbstractAbility {

    public PowerStrike() {
        super("Power Strike", "A powerful melee attack that deals bonus damage", 3, 15);
    }

    @Override
    public void activate(Character user, Character target, GameEngine engine)
            throws InvalidActionException, DeadCharacterException {
        if (!canUse(user)) {
            if (!isReady()) {
                throw new InvalidActionException("Power Strike is on cooldown for " + getRemainingCooldown() + " turns");
            } else {
                throw new InvalidActionException("Not enough mana. Need " + getManaCost() + " mana, have " + user.getCurrentMana());
            }
        }

        // Spend mana first
        if (!user.spendMana(getManaCost())) {
            throw new InvalidActionException("Failed to spend mana for Power Strike");
        }

        // Calculate bonus damage (50% more than regular attack)
        int baseDamage = user.getStats().getAttack() + user.getBaseDamage();
        int bonusDamage = baseDamage / 2;
        int totalDamage = baseDamage + bonusDamage;

        engine.log(user.getName() + " uses Power Strike on " + target.getName() + "!");
        engine.applyDamage(target, totalDamage);
        startCooldown();
        saveStateQuietly(engine);
    }
}
