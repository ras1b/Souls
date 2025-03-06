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
        this.loadConfig(); // Ensure config is loaded as soon as the object is created
    }

    public void loadConfig() {
        file = new File(plugin.getDataFolder(), "kills.yml");

        if (!file.exists()) {
            plugin.getLogger().warning("[Souls] Couldn't find an existing kills.yml, creating one...");
            plugin.getDataFolder().mkdirs();
            try {
                if (file.createNewFile()) {
                    plugin.getLogger().info("[Souls] Created new kills.yml file.");
                }
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
        return config.getInt(uuid.toString() + ".souls", 0);
    }

    public void addSouls(Player player, int soulsGained) {
        UUID uuid = player.getUniqueId();
        String playerPath = uuid.toString();
        String playerName = player.getName();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!config.contains(playerPath + ".souls")) {
                config.set(playerPath + ".souls", 0);
            }
            if (!config.contains(playerPath + ".name")) {
                config.set(playerPath + ".name", playerName);
            }

            // Ensure correct player name is stored
            if (!config.getString(playerPath + ".name", "Unknown").equals(playerName)) {
                config.set(playerPath + ".name", playerName);
            }

            // Update soul count
            config.set(playerPath + ".souls", getSouls(uuid) + soulsGained);
            saveConfig();
        });
    }


    public synchronized void updatePlayerName(Player player) {
        UUID uuid = player.getUniqueId();
        String playerPath = uuid.toString();
        String currentName = player.getName();

        if (!config.getString(playerPath + ".name", "Unknown").equals(currentName)) {
            config.set(playerPath + ".name", currentName);
            saveConfigAsync();
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

    public void saveConfigAsync() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this::saveConfig);
    }

    public void reload() {
        loadConfig();
        plugin.getLogger().info("[Souls] kills.yml reloaded successfully!");
    }
}
