package com.amin.battlearena.persistence;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// Manages saving and loading player data to/from JSON files
public class PlayerDataManager {
    private static final String SAVE_DIRECTORY = "saves";
    private static final String FILE_EXTENSION = ".json";
    private static volatile PlayerDataManager instance;
    private final Gson gson;
    private final Map<String, PlayerData> loadedData = new HashMap<>();
    
    private PlayerDataManager() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        ensureSaveDirectoryExists();
    }
    
    public static PlayerDataManager getInstance() {
        PlayerDataManager result = instance;
        if (result == null) {
            synchronized (PlayerDataManager.class) {
                result = instance;
                if (result == null) {
                    result = instance = new PlayerDataManager();
                }
            }
        }
        return result;
    }
    
    private void ensureSaveDirectoryExists() {
        try {
            Path saveDir = Paths.get(SAVE_DIRECTORY);
            if (!Files.exists(saveDir)) {
                Files.createDirectories(saveDir);
            }
        } catch (IOException e) {
            System.err.println("Failed to create save directory: " + e.getMessage());
        }
    }
    
    public PlayerData loadPlayerData(String playerName) {
        if (loadedData.containsKey(playerName)) {
            return loadedData.get(playerName);
        }
        
        Path filePath = Paths.get(SAVE_DIRECTORY, playerName + FILE_EXTENSION);
        
        if (Files.exists(filePath)) {
            try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(filePath), StandardCharsets.UTF_8)) {
                PlayerData data = gson.fromJson(reader, PlayerData.class);
                if (data != null) {
                    data.setPlayerName(playerName);
                    loadedData.put(playerName, data);
                    return data;
                }
            } catch (IOException e) {
                System.err.println("Failed to load player data for " + playerName + ": " + e.getMessage());
            }
        }
        
        // Create new player data if file doesn't exist
        PlayerData newData = new PlayerData(playerName);
        loadedData.put(playerName, newData);
        savePlayerData(newData);
        return newData;
    }
    
    public void savePlayerData(PlayerData playerData) {
        if (playerData == null || playerData.getPlayerName() == null) {
            return;
        }
        
        Path filePath = Paths.get(SAVE_DIRECTORY, playerData.getPlayerName() + FILE_EXTENSION);
        
        try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(filePath), StandardCharsets.UTF_8)) {
            gson.toJson(playerData, writer);
            loadedData.put(playerData.getPlayerName(), playerData);
            System.out.println("Player data saved for: " + playerData.getPlayerName());
        } catch (IOException e) {
            System.err.println("Failed to save player data for " + playerData.getPlayerName() + ": " + e.getMessage());
        }
    }
    
    public void saveAllLoadedData() {
        for (PlayerData data : loadedData.values()) {
            savePlayerData(data);
        }
    }
    
    public PlayerData getCurrentPlayerData(String playerName) {
        return loadedData.get(playerName);
    }
    
    public void clearLoadedData() {
        loadedData.clear();
    }
    
    public void resetPlayerData(String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) {
            return;
        }
        
        // Remove from memory
        loadedData.remove(playerName);
        
        // Delete save file
        Path filePath = Paths.get(SAVE_DIRECTORY, playerName + FILE_EXTENSION);
        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("Player data reset for: " + playerName);
            }
        } catch (IOException e) {
            System.err.println("Failed to delete save file for " + playerName + ": " + e.getMessage());
        }
        
        // Create fresh data
        PlayerData newData = new PlayerData(playerName);
        loadedData.put(playerName, newData);
        savePlayerData(newData);
    }
    
    public boolean playerExists(String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) {
            return false;
        }
        Path filePath = Paths.get(SAVE_DIRECTORY, playerName + FILE_EXTENSION);
        return Files.exists(filePath);
    }
    
    public java.util.List<String> getAllPlayerNames() {
        java.util.List<String> playerNames = new java.util.ArrayList<>();
        try {
            Path saveDir = Paths.get(SAVE_DIRECTORY);
            if (Files.exists(saveDir)) {
                Files.list(saveDir)
                    .filter(path -> path.toString().endsWith(FILE_EXTENSION))
                    .forEach(path -> {
                        String fileName = path.getFileName().toString();
                        String playerName = fileName.substring(0, fileName.length() - FILE_EXTENSION.length());
                        playerNames.add(playerName);
                    });
            }
        } catch (IOException e) {
            System.err.println("Failed to list player accounts: " + e.getMessage());
        }
        return playerNames;
    }
}