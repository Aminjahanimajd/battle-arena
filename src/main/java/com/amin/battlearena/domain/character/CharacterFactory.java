package com.amin.battlearena.domain.character;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class CharacterFactory {
    private static final Map<String, Function<int[], Character>> BUILDERS = new HashMap<>();
    
    static {
        BUILDERS.put("Warrior", stats -> createWarrior(stats[0], stats[1], stats[2], stats[3], stats[4], stats[5], stats[6]));
        BUILDERS.put("Archer", stats -> createArcher(stats[0], stats[1], stats[2], stats[3], stats[4], stats[5], stats[6]));
        BUILDERS.put("Mage", stats -> createMage(stats[0], stats[1], stats[2], stats[3], stats[4], stats[5], stats[6]));
    }
    
    private CharacterFactory() {}
    
    public static Character create(String type, int baseHp, int baseMana, int baseAttack, 
                                    int baseDefense, int baseRange, int baseSpeed, boolean isPlayerTeam) {
        int[] stats = {baseHp, baseMana, baseAttack, baseDefense, baseRange, baseSpeed, isPlayerTeam ? 1 : 0};
        
        Function<int[], Character> builder = BUILDERS.get(type);
        if (builder != null) {
            return builder.apply(stats);
        }
        throw new IllegalArgumentException("Unknown character type: " + type);
    }
    
    private static Character createWarrior(int hp, int mana, int atk, int def, int range, int spd, int isPlayer) {
        return new Warrior(hp, mana, atk, def, range, spd, isPlayer == 1);
    }
    
    private static Character createArcher(int hp, int mana, int atk, int def, int range, int spd, int isPlayer) {
        return new Archer(hp, mana, atk, def, range, spd, isPlayer == 1);
    }
    
    private static Character createMage(int hp, int mana, int atk, int def, int range, int spd, int isPlayer) {
        return new Mage(hp, mana, atk, def, range, spd, isPlayer == 1);
    }
}
