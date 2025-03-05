package com.ras1b.Souls.utils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

public class KillsConfig {

    private final JavaPlugin plugin;
    private File file;
    private FileConfiguration config;

    public KillsConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        this.loadConfig();  // Ensure config is loaded as soon as the object is created
    }

    public void loadConfig() {
        file = new File(plugin.getDataFolder(), "kills.yml");

        if (!file.exists()) {
            plugin.getLogger().warning("[Souls] Couldn't find an existing kills.yml, creating one...");
            plugin.getDataFolder().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("[Souls] Failed to create kills.yml!");
                e.printStackTrace();
            }
        } else {
            plugin.getLogger().info("[Souls] Loaded kills.yml successfully!");
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public int getKills(UUID uuid) {
        return config.getInt(uuid.toString() + ".kills", 0);
    }

    public synchronized int getSouls(UUID uuid) {
        if (config == null) {
            loadConfig();
        }
        if (!config.contains(uuid.toString())) {
            config.set(uuid.toString() + ".kills", 0);
            config.set(uuid.toString() + ".souls", 0);
            config.set(uuid.toString() + ".name", "Unknown");
            saveConfig();
        }
        return config.getInt(uuid.toString() + ".souls", 0);
    }

    public synchronized void addKill(Player player, int soulsGained) {
        UUID uuid = player.getUniqueId();
        String playerName = player.getName();

        if (!config.contains(uuid.toString())) {
            config.set(uuid.toString() + ".kills", 0);
            config.set(uuid.toString() + ".souls", 0);
            config.set(uuid.toString() + ".name", playerName);
        } else {
            String storedName = config.getString(uuid.toString() + ".name", "Unknown");
            if (!storedName.equals(playerName)) {
                config.set(uuid.toString() + ".name", playerName);
            }
        }

        config.set(uuid.toString() + ".kills", getKills(uuid) + 1);
        config.set(uuid.toString() + ".souls", getSouls(uuid) + soulsGained);
        saveConfig();
    }

    public synchronized void updatePlayerName(Player player) {
        UUID uuid = player.getUniqueId();
        String currentName = player.getName();

        if (config.contains(uuid.toString())) {
            String storedName = config.getString(uuid.toString() + ".name", "Unknown");
            if (!storedName.equals(currentName)) {
                config.set(uuid.toString() + ".name", currentName);
                saveConfig();
            }
        } else {
            config.set(uuid.toString() + ".name", currentName);
            saveConfig();
        }
    }
    
    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("[Souls] Failed to save kills.yml!");
            e.printStackTrace();
        }
    }

    public void reload() {
        loadConfig();
        plugin.getLogger().info("[Souls] kills.yml reloaded successfully!");
    }
}
