package com.amin.battlearena.progression;

import java.util.List;

public final class DefaultCampaign {
    public static CampaignMap create() {
        var map = new CampaignMap();
        map.add(new LevelNode("L01", "Forest Skirmish", List.of()));
        map.add(new LevelNode("L02", "Crossroads", List.of("L01")));
        map.add(new LevelNode("L03", "Ruined Keep", List.of("L02")));
        map.add(new LevelNode("L04", "Swamp Ambush", List.of("L03")));
        map.add(new LevelNode("L05", "Canyon Clash", List.of("L04")));
        map.add(new LevelNode("L06", "Bridge Siege", List.of("L05")));
        map.add(new LevelNode("L07", "Crystal Cavern", List.of("L06")));
        map.add(new LevelNode("L08", "Ashen Fields", List.of("L07")));
        map.add(new LevelNode("L09", "Frozen Ridge", List.of("L08")));
        map.add(new LevelNode("L10","Citadel Gate", List.of("L09")));
        return map;
    }
}
