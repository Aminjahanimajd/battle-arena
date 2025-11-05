package com.amin.battlearena.domain.level.factory;

import java.util.ArrayList;
import java.util.List;

import com.amin.battlearena.domain.level.LevelSpec;
import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.domain.model.CharacterFactory;
import com.amin.battlearena.domain.model.Position;

// Builds enemy teams from LevelSpec using CharacterFactory
public final class TeamFactory {

    public static List<Character> buildEnemies(LevelSpec spec) {
        List<Character> enemies = new ArrayList<>();
        List<String> types = spec.enemies();
        List<List<Integer>> positions = spec.enemyPositions();
        for (int i = 0; i < types.size(); i++) {
            String t = types.get(i);
            List<Integer> pos = positions.get(i);
            Position p = new Position(pos.get(0), pos.get(1));
            // Use CharacterFactory instead of direct instantiation
            enemies.add(CharacterFactory.create(t, "Enemy-" + (i + 1), p));
        }
        return enemies;
    }
}