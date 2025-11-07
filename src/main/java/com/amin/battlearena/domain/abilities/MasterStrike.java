package com.amin.battlearena.domain.abilities;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;
import com.amin.battlearena.infra.CharacterBalanceConfig;
import com.amin.battlearena.infra.CharacterBalanceConfig.AbilityConfig;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.infra.InvalidActionException;

// Master ability: devastating attack with massive damage
// Stats loaded from balance.json via CharacterBalanceConfig
public final class MasterStrike extends AbstractAbility {

    private static final AbilityConfig CONFIG = CharacterBalanceConfig.getInstance().getAbilityConfig("MasterStrike");

    public MasterStrike() {
        super(CONFIG.getName(), CONFIG.getDescription(), CONFIG.getCooldown(), CONFIG.getManaCost(), CONFIG.getRange());
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
