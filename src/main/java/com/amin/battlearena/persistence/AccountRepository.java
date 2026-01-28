package com.amin.battlearena.persistence;

import com.amin.battlearena.domain.account.Player;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class AccountRepository {
    private static AccountRepository instance;
    private Player currentUser;
    private final String SAVE_FILE = "savegame.dat";

    public static AccountRepository getInstance() {
        if (instance == null) {
            instance = new AccountRepository();
        }
        return instance;
    }

    public Player login(String nickname) {
        // Try to load existing player
        Player player = loadPlayer(nickname);
        if (player == null) {
            // Create new player
            player = new Player(nickname);
            savePlayer(player);
        }
        currentUser = player;
        return player;
    }

    public Player getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        if (currentUser != null) {
            savePlayer(currentUser);
            currentUser = null;
        }
    }

    public void savePlayer(Player player) {
        // Simple implementation: Read all, update/add one, write back
        // For simplicity in this project, we might just append or rewrite.
        // Since we want to avoid complexity, let's just use a Map in memory and dump to file.
        
        Map<String, String> allData = loadAllData();
        allData.put(player.getNickname(), serialize(player));
        
        try (PrintWriter out = new PrintWriter(new FileWriter(SAVE_FILE))) {
            for (String line : allData.values()) {
                out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Player loadPlayer(String nickname) {
        Map<String, String> allData = loadAllData();
        if (allData.containsKey(nickname)) {
            return deserialize(allData.get(nickname));
        }
        return null;
    }

    private Map<String, String> loadAllData() {
        Map<String, String> data = new HashMap<>();
        File file = new File(SAVE_FILE);
        if (!file.exists()) return data;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length > 0) {
                    data.put(parts[0], line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private String serialize(Player p) {
        StringBuilder sb = new StringBuilder();
        sb.append(p.getNickname()).append("|");
        sb.append(p.getGold()).append("|");
        sb.append(p.getLevel()).append("|");
        sb.append(p.getVictories()).append("|");
        sb.append(p.getCampaignProgress()).append("|");
        
        // Upgrades
        for (int i = 0; i < 9; i++) {
            sb.append(p.getUpgradeLevel(i));
            if (i < 8) sb.append(",");
        }
        sb.append("|");
        
        // Inventory
        Map<String, Integer> inv = p.getInventory();
        if (inv.isEmpty()) {
            sb.append("EMPTY");
        } else {
            int count = 0;
            for (Map.Entry<String, Integer> entry : inv.entrySet()) {
                sb.append(entry.getKey()).append(":").append(entry.getValue());
                count++;
                if (count < inv.size()) sb.append(",");
            }
        }
        
        return sb.toString();
    }

    private Player deserialize(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 7) return null;

        Player p = new Player(parts[0]);
        p.setGold(Integer.parseInt(parts[1]));
        p.setLevel(Integer.parseInt(parts[2]));
        p.setVictories(Integer.parseInt(parts[3]));
        p.setCampaignProgress(Integer.parseInt(parts[4]));

        // Upgrades
        String[] ups = parts[5].split(",");
        int[] upgrades = new int[9];
        for (int i = 0; i < ups.length && i < 9; i++) {
            upgrades[i] = Integer.parseInt(ups[i]);
        }
        p.setUpgrades(upgrades);

        // Inventory
        if (!parts[6].equals("EMPTY")) {
            Map<String, Integer> inv = new HashMap<>();
            String[] items = parts[6].split(",");
            for (String item : items) {
                String[] pair = item.split(":");
                inv.put(pair[0], Integer.parseInt(pair[1]));
            }
            p.setInventory(inv);
        }

        return p;
    }
}
