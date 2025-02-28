package com.ras1b.Souls.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class KillsConfig {

    private final JavaPlugin plugin;
    private File file;
    private FileConfiguration config;

    public KillsConfig(JavaPlugin plugin) {
        this.plugin = plugin;
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
        return config.getInt(uuid.toString() + ".kills", 0);
    }

    public int getSouls(UUID uuid) {
        return config.getInt(uuid.toString() + ".souls", 0);
    }

    public void addKill(UUID uuid) {
        int kills = getKills(uuid) + 1;
        int souls = getSouls(uuid) + 5; // Gain 5 souls per kill
        config.set(uuid.toString() + ".kills", kills);
        config.set(uuid.toString() + ".souls", souls);
        saveConfig();
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
