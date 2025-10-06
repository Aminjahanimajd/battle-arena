package com.amin.battlearena.domain.abilities;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.infra.InvalidActionException;

/**
 * Archer ability: fires two arrows in quick succession.
 */
public final class DoubleShot extends AbstractAbility {

    public DoubleShot() {
        super("Double Shot", "Fire two arrows in quick succession", 4, 18);
    }

    @Override
    public void activate(Character user, Character target, GameEngine engine)
            throws InvalidActionException, DeadCharacterException {
        if (!canUse(user)) {
            if (!isReady()) {
                throw new InvalidActionException("Double Shot is on cooldown for " + getRemainingCooldown() + " turns");
            } else {
                throw new InvalidActionException("Not enough mana. Need " + getManaCost() + " mana, have " + user.getCurrentMana());
            }
        }

        if (target == null) {
            throw new InvalidActionException("No target for Double Shot");
        }

        // Spend mana first
        if (!user.spendMana(getManaCost())) {
            throw new InvalidActionException("Failed to spend mana for Double Shot");
        }

        // Fire two shots
        int baseDamage = user.getStats().getAttack() + user.getBaseDamage();
        
        engine.log(user.getName() + " uses Double Shot on " + target.getName() + "!");
        
        // First shot
        engine.applyDamage(target, baseDamage);
        
        // Second shot (slightly reduced damage)
        int secondShotDamage = (int) (baseDamage * 0.8);
        engine.applyDamage(target, secondShotDamage);
        
        startCooldown();
        saveStateQuietly(engine);
    }
}
