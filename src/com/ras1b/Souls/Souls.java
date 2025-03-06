package com.ras1b.Souls;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.ras1b.Commands.SoulsCommand;
import com.ras1b.Placeholders.SoulsPlaceholder;
import com.ras1b.Souls.utils.KillsConfig;

public class Souls extends JavaPlugin {

    private static Souls instance;
    private KillsConfig killsConfig;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("[Souls] Plugin has been enabled!");

        // Ensure config folder exists
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Ensure config.yml exists inside the plugin folder
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveResource("config.yml", false);
        }

        reloadConfig();
        killsConfig = new KillsConfig(this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new SoulsPlaceholder(this).register();
            getLogger().info("[Souls] Successfully registered Souls placeholder!");
        } else {
            getLogger().warning("[Souls] PlaceholderAPI not found! The placeholder won't work.");
        }

        if (getCommand("souls") != null) {
            getCommand("souls").setExecutor(new SoulsCommand(this));
        } else {
            getLogger().warning("[Souls] Command 'souls' not found in plugin.yml!");
        }

        getServer().getPluginManager().registerEvents(new PlayerKillListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(killsConfig), this);

        loadKillsConfig();

        getLogger().info("[Souls] Plugin setup complete!");
    }


    @Override
    public void onDisable() {
        getLogger().info("[Souls] Plugin has been disabled!");
    }

    private void loadKillsConfig() {
        if (killsConfig == null) {
            killsConfig = new KillsConfig(this);
            getLogger().info("[Souls] Successfully loaded Kills config.");
        }
    }

    public KillsConfig getKillsConfig() {
        return killsConfig;
    }


    public static Souls getInstance() {
        return instance;
    }

    public void reloadPlugin() {
        getLogger().info("[Souls] Reloading plugin...");
        reloadConfig(); // Reload config.yml

        // Run leaderboard update asynchronously
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            SoulsPlaceholder.updateTopSouls();
            getLogger().info("[Souls] Leaderboard successfully updated!");
        });

        getLogger().info("[Souls] Plugin reload complete!");
    }



}
