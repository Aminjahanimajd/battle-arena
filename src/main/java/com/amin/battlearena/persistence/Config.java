package com.amin.battlearena.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

// Small configuration loader with multiple precedence levels
public final class Config {

    private Config() {}

    public static String get(String key, String envKey, String defaultValue) {
        String fromProp = System.getProperty(key);
        if (fromProp != null && !fromProp.isBlank()) return fromProp;

        String fromEnv = System.getenv(envKey);
        if (fromEnv != null && !fromEnv.isBlank()) return fromEnv;

        String fromWorking = loadFromWorkingDir(key);
        if (fromWorking != null) return fromWorking;

        String fromClasspath = loadFromClasspath(key);
        if (fromClasspath != null) return fromClasspath;

        return defaultValue;
    }

    private static String loadFromWorkingDir(String key) {
        Properties p = new Properties();
        Path primary = Paths.get("config", "application.properties");
        Path secondary = Paths.get("application.properties");
        if (Files.exists(primary)) {
            try (InputStream in = Files.newInputStream(primary)) {
                p.load(in);
                String v = p.getProperty(key);
                if (v != null && !v.isBlank()) return v;
            } catch (IOException ignored) {}
        }
        if (Files.exists(secondary)) {
            try (InputStream in = Files.newInputStream(secondary)) {
                p.load(in);
                String v = p.getProperty(key);
                if (v != null && !v.isBlank()) return v;
            } catch (IOException ignored) {}
        }
        return null;
    }

    private static String loadFromClasspath(String key) {
        Properties p = new Properties();
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in == null) return null;
            p.load(in);
            String v = p.getProperty(key);
            if (v != null && !v.isBlank()) return v;
        } catch (IOException ignored) {}
        return null;
    }
}


