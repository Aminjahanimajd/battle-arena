package com.amin.battlearena.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.amin.battlearena.progression.PlayerProgress;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

// ProgressService persists PlayerProgress to JSON files
public final class ProgressService {

    private final PlayerDAO dao;
    private final Path savesDir;
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public ProgressService() {
        this(new PlayerDAO(), Paths.get("saves"));
    }

    public ProgressService(PlayerDAO dao, Path savesDir) {
        this.dao = dao;
        this.savesDir = savesDir != null ? savesDir : Paths.get("saves");
    }

    private Path jsonFile(String playerName) {
        return savesDir.resolve(playerName + ".json");
    }

    public PlayerProgress load(String playerName) {
        Path file = jsonFile(playerName);

        // 1) try JSON file
        if (Files.exists(file)) {
            try {
                PlayerProgress progress = mapper.readValue(Files.newBufferedReader(file), PlayerProgress.class);
                if (progress.playerId() == null || progress.playerId().isBlank()) progress.setPlayerId(playerName);
                return progress;
            } catch (IOException e) {
                throw new RuntimeException("Failed to read progress JSON for " + playerName, e);
            }
        }

        // 2) fallback to DB
        PlayerProgress progress = new PlayerProgress(playerName);
        dao.findGold(playerName).ifPresent(progress.wallet()::add);
        dao.findUnlockedLevel(playerName).ifPresent(n -> progress.unlockLevel(String.format("L%02d", Math.max(1, n))));

        // ensure default unlocked level
        if (progress.unlockedLevels().isEmpty()) progress.unlockLevel("L01");
        return progress;
    }

    public void save(String playerName, PlayerProgress progress) {
        try {
            Files.createDirectories(savesDir);
            Path file = jsonFile(playerName);
            if (progress.playerId() == null || progress.playerId().isBlank()) progress.setPlayerId(playerName);
            mapper.writeValue(Files.newBufferedWriter(file, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING), progress);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save progress JSON for " + playerName, e);
        }

        // sync to DB: gold and numeric unlocked_level (max numeric)
        int gold = progress.wallet().gold();
        int numeric = progress.unlockedLevels().stream()
                .map(s -> {
                    try {
                        if (s.startsWith("L") || s.startsWith("l")) return Integer.parseInt(s, 1, s.length(), 10);
                        return Integer.parseInt(s);
                    } catch (NumberFormatException ex) { return 1; }
                })
                .max(Integer::compareTo).orElse(1);
        dao.createOrUpdatePlayer(playerName, gold, Math.max(1, numeric), String.join(",", progress.unlockedLevels()));
    }
}
