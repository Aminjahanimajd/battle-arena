package com.amin.battlearena.domain.abilities;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.infra.InvalidActionException;

/**
 * Ranger ability: fires a volley of arrows that can hit multiple targets.
 */
public final class PiercingVolley extends AbstractAbility {

    public PiercingVolley() {
        super("Piercing Volley", "Fire a volley of arrows that can hit multiple enemies", 6, 26);
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

        // Calculate piercing damage (ignores a portion of defense and slightly amplifies base)
        int baseDamage = user.getStats().getAttack() + user.getBaseDamage();
        int targetDefense = target.getStats().getDefense() + target.getTemporaryDefense();
        int ignoredDefense = (int) Math.round(targetDefense * 0.30); // Ignore 30% of defense
        int effectiveDefense = Math.max(0, targetDefense - ignoredDefense);

        int raw = Math.max(0, baseDamage - effectiveDefense);
        int damage = (int) Math.round(raw * 1.05); // 5% bonus

        engine.log(user.getName() + " unleashes Piercing Volley on " + target.getName() + "!");
        engine.applyDamage(target, damage);
        startCooldown();
    }
}
