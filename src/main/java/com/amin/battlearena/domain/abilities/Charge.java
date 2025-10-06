package com.amin.battlearena.domain.abilities;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.domain.model.Position;
import com.amin.battlearena.engine.core.GameEngine;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.infra.InvalidActionException;

/**
 * Knight ability: charge towards an enemy and deal bonus damage.
 */
public final class Charge extends AbstractAbility {

    public Charge() {
        super("Charge", "Charge towards an enemy and deal bonus damage", 4, 20);
    }

    @Override
    public void activate(Character user, Character target, GameEngine engine)
            throws InvalidActionException, DeadCharacterException {
        if (!canUse(user)) {
            if (!isReady()) {
                throw new InvalidActionException("Charge is on cooldown for " + getRemainingCooldown() + " turns");
            } else {
                throw new InvalidActionException("Not enough mana. Need " + getManaCost() + " mana, have " + user.getCurrentMana());
            }
        }

        if (target == null) {
            throw new InvalidActionException("No target for Charge");
        }

        // Spend mana first
        if (!user.spendMana(getManaCost())) {
            throw new InvalidActionException("Failed to spend mana for Charge");
        }

        // Move towards target (up to 2 spaces)
        Position currentPos = user.getPosition();
        Position targetPos = target.getPosition();
        
        // Calculate movement (up to 2 spaces towards target)
        int dx = Integer.compare(targetPos.x(), currentPos.x());
        int dy = Integer.compare(targetPos.y(), currentPos.y());
        
        int newX = currentPos.x() + (dx * Math.min(2, Math.abs(targetPos.x() - currentPos.x())));
        int newY = currentPos.y() + (dy * Math.min(2, Math.abs(targetPos.y() - currentPos.y())));
        
        Position newPos = new Position(newX, newY);
        engine.move(user, newPos);

        // Deal bonus damage if we're now adjacent
        if (user.getPosition().distanceTo(target.getPosition()) <= 1) {
            int baseDamage = user.getStats().getAttack() + user.getBaseDamage();
            int bonusDamage = baseDamage / 2; // 50% bonus
            int totalDamage = baseDamage + bonusDamage;
            
            engine.log(user.getName() + " charges into " + target.getName() + "!");
            engine.applyDamage(target, totalDamage);
        } else {
            engine.log(user.getName() + " charges towards " + target.getName() + "!");
        }

        startCooldown();
        saveStateQuietly(engine);
    }
}
