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

        if (file.exists()) {
            plugin.getLogger().info("[Souls] Loaded kills.yml successfully!");
        } else {
            plugin.getLogger().warning("[Souls] Couldn't find an existing kills.yml, creating one...");
            plugin.getDataFolder().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("[Souls] Failed to create kills.yml!");
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public int getKills(UUID uuid) {
        // Check if the UUID exists in the config, otherwise return 0 kills
        if (config.contains(uuid.toString())) {
            return config.getInt(uuid.toString() + ".kills", 0);
        }
        return 0; // Return 0 if the UUID is not found
    }

    public synchronized int getSouls(UUID uuid) {
        // Ensure config is loaded before accessing it
        if (config == null) {
            loadConfig();  // Reload config if it's null
        }

        // Check if the player exists in the config, otherwise initialize them with 0 souls
        if (!config.contains(uuid.toString())) {
            config.set(uuid.toString() + ".kills", 0); // Initialize with 0 kills
            config.set(uuid.toString() + ".souls", 0); // Initialize with 0 souls
            config.set(uuid.toString() + ".name", "Unknown"); // Initialize player name as Unknown
            saveConfig();  // Save immediately after initialization
        }

        return config.getInt(uuid.toString() + ".souls", 0);  // Return souls, default to 0 if not found
    }

    public synchronized void addKill(Player player, int soulsGained) {
        UUID uuid = player.getUniqueId();
        String playerName = player.getName();

        // Ensure the player's data exists in the config
        if (!config.contains(uuid.toString())) {
            config.set(uuid.toString() + ".kills", 0); // Initialize kills
            config.set(uuid.toString() + ".souls", 0); // Initialize souls
            config.set(uuid.toString() + ".name", playerName); // Store player name
        } else {
            // Update name if it has changed
            String storedName = config.getString(uuid.toString() + ".name", "Unknown");
            if (!storedName.equals(playerName)) {
                config.set(uuid.toString() + ".name", playerName);
            }
        }

        // Increment the kill count and soul count
        int kills = getKills(uuid) + 1;
        int souls = getSouls(uuid) + soulsGained;

        // Update the player's kills and souls in the config
        config.set(uuid.toString() + ".kills", kills);
        config.set(uuid.toString() + ".souls", souls);

        // Save the updated config to persist changes
        saveConfig();
    }


    // Update player name when they join or when their name changes
    public synchronized void updatePlayerName(Player player) {
        UUID uuid = player.getUniqueId();
        String currentName = player.getName();

        // Check if the name is different in the config
        if (config.contains(uuid.toString())) {
            String storedName = config.getString(uuid.toString() + ".name", "Unknown");
            if (!storedName.equals(currentName)) {
                // Update player name in config
                config.set(uuid.toString() + ".name", currentName);
                saveConfig();
            }
        } else {
            // If player data doesn't exist in config, initialize with name
            config.set(uuid.toString() + ".name", currentName);
            saveConfig();
        }
    }
    
    private void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("[Souls] Failed to save kills.yml!");
            e.printStackTrace();
        }
    }
}
