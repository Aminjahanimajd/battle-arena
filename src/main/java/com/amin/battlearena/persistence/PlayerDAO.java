package com.amin.battlearena.persistence;

import com.amin.battlearena.database.DatabaseManager;

import java.sql.*;
import java.util.Optional;

/**
 * Basic player CRUD tailored for the battle-arena demo.
 */
public final class PlayerDAO {

    public void createOrUpdatePlayer(String name, int gold, int unlockedLevel) {
        String sql = """
            INSERT INTO players(name, gold, unlocked_level)
            VALUES(?, ?, ?)
            ON CONFLICT(name) DO UPDATE SET gold=excluded.gold, unlocked_level=excluded.unlocked_level
            """;
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, gold);
            ps.setInt(3, unlockedLevel);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Integer> findGold(String name) {
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT gold FROM players WHERE name = ?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(rs.getInt("gold"));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void recordMatch(String playerName, int level, String result, int goldEarned) {
        String find = "SELECT id FROM players WHERE name = ?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement psFind = c.prepareStatement(find)) {
            psFind.setString(1, playerName);
            try (ResultSet rs = psFind.executeQuery()) {
                if (rs.next()) {
                    int pid = rs.getInt("id");
                    try (PreparedStatement ps = c.prepareStatement(
                            "INSERT INTO match_history(player_id, result, kills, gold_earned) VALUES(?, ?, ?, ?)")) {
                        ps.setInt(1, pid);
                        ps.setString(2, result);
                        ps.setInt(3, 0);
                        ps.setInt(4, goldEarned);
                        ps.executeUpdate();
                    }
                } else {
                    // player missing: optionally create
                    createOrUpdatePlayer(playerName, goldEarned, 1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
