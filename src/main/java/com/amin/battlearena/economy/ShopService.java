package com.amin.battlearena.economy;

import java.util.Optional;

import com.amin.battlearena.domain.abilities.Ability;
import com.amin.battlearena.domain.abilities.AbilityFactory;
import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.persistence.PlayerDAO;
import com.amin.battlearena.players.Player;

/**
 * Simple shop service that applies upgrades to characters and (optionally) persists player gold/records.
 *
 * Usage:
 * - call purchaseStatUpgrade(player, characterName, "attack", delta, cost)
 * - call purchaseAbility(player, characterName, "PowerStrike", cost)
 *
 * This implementation assumes PlayerDAO manages player gold externally; here we only mutate in-memory characters.
 */
public final class ShopService {

    private final PlayerDAO playerDao = new PlayerDAO();

    public ShopService() {}

    public boolean purchaseStatUpgrade(String playerName, Player player, String characterName,
                                       String statKey, int delta, int cost) {
        if (player == null) throw new IllegalArgumentException("player null");
        Optional<Character> opt = player.getTeam().stream()
                .filter(c -> c.getName().equalsIgnoreCase(characterName))
                .findFirst();
        if (opt.isEmpty()) return false;
        Character c = opt.get();

        String lowerStatKey = statKey.toLowerCase(java.util.Locale.ROOT);
        if ("hp".equals(lowerStatKey)) {
            c.getStats().modifyMaxHp(delta);
        } else if ("attack".equals(lowerStatKey)) {
            c.getStats().modifyAttack(delta);
        } else if ("defense".equals(lowerStatKey)) {
            c.getStats().modifyDefense(delta);
        } else if ("range".equals(lowerStatKey)) {
            c.getStats().setRange(Math.max(1, c.getStats().getRange() + delta));
            // 'speed' removed: movement per turn replaces speed concept
        } else {
            throw new IllegalArgumentException("Unknown stat: " + statKey);
        }

        // persist/update player's gold (basic example: overwrite with decreased gold)
        playerDao.createOrUpdatePlayer(playerName, Math.max(0, getPlayerGold(playerName) - cost), 1);
        return true;
    }

    public boolean purchaseAbility(String playerName, Player player, String characterName, String abilityKey, int cost) {
        if (player == null) throw new IllegalArgumentException("player null");
        Optional<Character> opt = player.getTeam().stream()
                .filter(c -> c.getName().equalsIgnoreCase(characterName))
                .findFirst();
        if (opt.isEmpty()) return false;
        Character c = opt.get();

        Ability ability = AbilityFactory.create(abilityKey);
        if (ability == null) return false; // keep single check in case factory returns null for unknown keys
        c.addAbility(ability);

        playerDao.createOrUpdatePlayer(playerName, Math.max(0, getPlayerGold(playerName) - cost), 1);
        return true;
    }

    private int getPlayerGold(String playerName) {
        return playerDao.findGold(playerName).orElse(0);
    }
}
