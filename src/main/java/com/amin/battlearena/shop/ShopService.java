package com.amin.battlearena.shop;

import com.amin.battlearena.abilities.Ability;
import com.amin.battlearena.abilities.AbilityFactory;
import com.amin.battlearena.model.Character;
import com.amin.battlearena.persistence.PlayerDAO;
import com.amin.battlearena.player.Player;
import java.util.Optional;

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

        switch (statKey.toLowerCase()) {
            case "hp" -> c.getStats().modifyMaxHp(delta);
            case "attack" -> c.getStats().modifyAttack(delta);
            case "defense" -> c.getStats().modifyDefense(delta);
            case "speed" -> c.getStats().modifySpeed(delta);
            default -> throw new IllegalArgumentException("Unknown stat: " + statKey);
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
        if (ability == null) return false;
        c.addAbility(ability);

        playerDao.createOrUpdatePlayer(playerName, Math.max(0, getPlayerGold(playerName) - cost), 1);
        return true;
    }

    private int getPlayerGold(String playerName) {
        return playerDao.findGold(playerName).orElse(0);
    }
}
