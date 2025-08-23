package com.amin.battlearena.progression;

import java.util.List;

import com.amin.battlearena.domain.model.Character;

public interface Level {
    String id();
    String name();
    LevelRewards rewards();
    WinCondition condition();
    List<Character> playerTeam(); // prebuilt teams (positions included)
    List<Character> enemyTeam();
    List<String> prereqs();       // level ids that must be beaten
}
