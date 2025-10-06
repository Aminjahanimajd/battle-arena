package com.amin.battlearena.domain.abilities;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.infra.InvalidActionException;

/**
 * Mage ability: powerful magical burst attack.
 */
public final class ArcaneBurst extends AbstractAbility {

    public ArcaneBurst() {
        super("Arcane Burst", "A powerful magical attack that ignores some defense", 5, 25, 3);
    }

    @Override
    public void activate(Character user, Character target, GameEngine engine)
            throws InvalidActionException, DeadCharacterException {
        if (!canUse(user)) {
            if (!isReady()) {
                throw new InvalidActionException("Arcane Burst is on cooldown for " + getRemainingCooldown() + " turns");
            } else {
                throw new InvalidActionException("Not enough mana. Need " + getManaCost() + " mana, have " + user.getCurrentMana());
            }
        }

        if (target == null) {
            throw new InvalidActionException("No target for Arcane Burst");
        }

        // Check range
        if (!isInRange(user, target)) {
            throw new InvalidActionException("Target is out of range for Arcane Burst (max range: " + getRange() + ")");
        }

        // Spend mana first
        if (!user.spendMana(getManaCost())) {
            throw new InvalidActionException("Failed to spend mana for Arcane Burst");
        }

        // Calculate magical damage (ignores 50% of target's defense)
        int baseDamage = user.getStats().getAttack() + user.getBaseDamage();
        int targetDefense = target.getStats().getDefense() + target.getTemporaryDefense();
        int ignoredDefense = targetDefense / 2; // Ignore half the defense
        int effectiveDefense = targetDefense - ignoredDefense;
        
        int damage = Math.max(0, baseDamage - effectiveDefense);

        engine.log(user.getName() + " unleashes Arcane Burst on " + target.getName() + "!");
        engine.applyDamage(target, damage);
        startCooldown();
        saveStateQuietly(engine);
    }
}
