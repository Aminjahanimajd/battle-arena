package com.amin.battlearena.domain.abilities;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;
import com.amin.battlearena.infra.CharacterBalanceConfig;
import com.amin.battlearena.infra.CharacterBalanceConfig.AbilityConfig;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.infra.InvalidActionException;

// Master ability: grants temporary evasion chance
// Stats loaded from balance.json via CharacterBalanceConfig
public final class Evasion extends AbstractAbility {

    private static final AbilityConfig CONFIG = CharacterBalanceConfig.getInstance().getAbilityConfig("Evasion");

    public Evasion() {
        super(CONFIG.getName(), CONFIG.getDescription(), CONFIG.getCooldown(), CONFIG.getManaCost(), CONFIG.getRange());
    }

    @Override
    public void activate(Character user, Character target, GameEngine engine)
            throws InvalidActionException, DeadCharacterException {
        if (!canUse(user)) {
            if (!isReady()) {
                throw new InvalidActionException("Evasion is on cooldown for " + getRemainingCooldown() + " turns");
            } else {
                throw new InvalidActionException("Not enough mana. Need " + getManaCost() + " mana, have " + user.getCurrentMana());
            }
        }

        // Spend mana first
        if (!user.spendMana(getManaCost())) {
            throw new InvalidActionException("Failed to spend mana for Evasion");
        }

        // Grant 30% evasion chance for this turn
        user.addTemporaryEvasion(0.3);
        engine.log(user.getName() + " activates Evasion! (30% chance to dodge attacks)");
        startCooldown();
        saveStateQuietly(engine);
    }
}
