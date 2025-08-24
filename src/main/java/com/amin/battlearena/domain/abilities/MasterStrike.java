package com.amin.battlearena.domain.abilities;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.infra.InvalidActionException;

/**
 * Master ability: devastating attack that deals massive damage.
 */
public final class MasterStrike extends AbstractAbility {

    public MasterStrike() {
        super("Master Strike", "A devastating attack that deals massive damage", 8, 50);
    }

    @Override
    public void activate(Character user, Character target, GameEngine engine)
            throws InvalidActionException, DeadCharacterException {
        if (!canUse(user)) {
            if (!isReady()) {
                throw new InvalidActionException("Master Strike is on cooldown for " + getRemainingCooldown() + " turns");
            } else {
                throw new InvalidActionException("Not enough mana. Need " + getManaCost() + " mana, have " + user.getCurrentMana());
            }
        }

        if (target == null) {
            throw new InvalidActionException("No target for Master Strike");
        }

        // Spend mana first
        if (!user.spendMana(getManaCost())) {
            throw new InvalidActionException("Failed to spend mana for Master Strike");
        }

        // Calculate devastating damage (200% of normal attack)
        int baseDamage = user.getStats().getAttack() + user.getBaseDamage();
        int totalDamage = baseDamage * 2;

        engine.log(user.getName() + " unleashes Master Strike on " + target.getName() + "!");
        engine.applyDamage(target, totalDamage);
        startCooldown();
    }
}
