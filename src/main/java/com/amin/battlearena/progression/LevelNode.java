package com.amin.battlearena.progression;

import java.util.List;

public record LevelNode(String id, String name, List<String> prereqs) {}
