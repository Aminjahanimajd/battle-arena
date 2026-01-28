package com.amin.battlearena.domain.team;

import com.amin.battlearena.domain.account.AccountRepository;
import com.amin.battlearena.domain.account.Player;
import com.amin.battlearena.domain.character.Character;
import com.amin.battlearena.domain.character.CharacterFactory;

public final class HumanTeam extends Team {
    private static final int[] BASE_STATS = {
        100, 50, 15, 5, 1, 3,      // Warrior: hp, mana, atk, def, range, spd
        80, 60, 12, 2, 3, 4,       // Archer
        60, 100, 20, 1, 2, 3       // Mage
    };
    
    private static final int[][] MULTIPLIERS = {
        {20, 5, 3, 1, 1, 5, 10, 5, 1},    // Warrior: Health, Attack, Defense, Range, Speed, AttacksPerTurn (not used), Mana, SpellPower, Cooldown
        {15, 4, 2, 1, 1, 5, 10, 5, 1},    // Archer
        {10, 2, 1, 1, 1, 5, 20, 8, 1}     // Mage
    };
    
    public HumanTeam() {
        super(true);
    }
    
    @Override
    public void initialize() {
        Player player = AccountRepository.getInstance().getCurrentUser();
        if (player == null) return;
        
        addMember(createCharacter(0, "Warrior", player));
        addMember(createCharacter(1, "Archer", player));
        addMember(createCharacter(2, "Mage", player));
    }
    
    private Character createCharacter(int typeIndex, String type, Player player) {
        int baseIdx = typeIndex * 6;
        int hp = BASE_STATS[baseIdx] + applyUpgrades(0, typeIndex, player);
        int mana = BASE_STATS[baseIdx + 1] + applyUpgrades(6, typeIndex, player);
        int atk = BASE_STATS[baseIdx + 2] + applyUpgrades(1, typeIndex, player);
        int def = BASE_STATS[baseIdx + 3] + applyUpgrades(2, typeIndex, player);
        int range = BASE_STATS[baseIdx + 4] + applyUpgrades(3, typeIndex, player);
        int spd = BASE_STATS[baseIdx + 5] + applyUpgrades(4, typeIndex, player);
        
        return CharacterFactory.create(type, hp, mana, atk, def, range, spd, true);
    }
    
    private int applyUpgrades(int upgradeType, int typeIndex, Player player) {
        int level = player.getUpgradeLevel(upgradeType);
        return level * MULTIPLIERS[typeIndex][upgradeType];
    }
}
