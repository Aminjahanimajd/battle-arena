package com.amin.battlearena.domain.abilities;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.infra.InvalidActionException;

/**
 * Ranger ability: fires a volley of arrows that can hit multiple targets.
 */
public final class PiercingVolley extends AbstractAbility {

    public PiercingVolley() {
        super("Piercing Volley", "Fire a volley of arrows that can hit multiple enemies", 6, 30);
    }

    @Override
    public void activate(Character user, Character target, GameEngine engine)
            throws InvalidActionException, DeadCharacterException {
        if (!canUse(user)) {
            if (!isReady()) {
                throw new InvalidActionException("Piercing Volley is on cooldown for " + getRemainingCooldown() + " turns");
            } else {
                throw new InvalidActionException("Not enough mana. Need " + getManaCost() + " mana, have " + user.getCurrentMana());
            }
        }

        if (target == null) {
            throw new InvalidActionException("No target for Piercing Volley");
        }

        // Spend mana first
        if (!user.spendMana(getManaCost())) {
            throw new InvalidActionException("Failed to spend mana for Piercing Volley");
        }

        // Calculate piercing damage (ignores some defense)
        int baseDamage = user.getStats().getAttack() + user.getBaseDamage();
        int targetDefense = target.getStats().getDefense() + target.getTemporaryDefense();
        int ignoredDefense = targetDefense / 3; // Ignore 1/3 of the defense
        int effectiveDefense = targetDefense - ignoredDefense;
        
        int damage = Math.max(0, baseDamage - effectiveDefense);

        engine.log(user.getName() + " unleashes Piercing Volley on " + target.getName() + "!");
        engine.applyDamage(target, damage);
        startCooldown();
    }
}
