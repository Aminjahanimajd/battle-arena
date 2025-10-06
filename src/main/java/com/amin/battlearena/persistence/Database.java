package com.amin.battlearena.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    private static final String DEFAULT_URL = "jdbc:sqlite:battle_arena.db";
    private static final String ENV_KEY = "BATTLE_ARENA_JDBC_URL";
    private static final String PROP_KEY = "battlearena.jdbcUrl";

    private Database() {}

    public static Connection getConnection() throws SQLException {
        String url = Config.get(PROP_KEY, ENV_KEY, DEFAULT_URL);
        return DriverManager.getConnection(url);
    }
}
