package com.ras1b.Souls;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.ras1b.Commands.SoulsCommand;
import com.ras1b.Placeholders.SoulsHologramManager;
import com.ras1b.Placeholders.SoulsPlaceholder;
import com.ras1b.Souls.utils.KillsConfig;

public class Souls extends JavaPlugin {

    private KillsConfig killsConfig;

    @Override
    public void onEnable() {
        getLogger().info("[Souls] Plugin has been enabled!");

        // Save the default config if it doesn't exist
        saveDefaultConfig(); // Ensures config.yml is created if missing

        // Initialize killsConfig (for managing the kills.yml file)
        killsConfig = new KillsConfig(this);

        // Register PlaceholderAPI expansion if available
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new SoulsPlaceholder(this).register();
            getLogger().info("[Souls] Successfully registered Souls placeholder!");
        } else {
            getLogger().warning("[Souls] PlaceholderAPI not found! The placeholder won't work.");
        }

        // Register the /souls command and link it to the SoulsCommand executor
        if (getCommand("souls") != null) {
            getCommand("souls").setExecutor(new SoulsCommand(this));
        } else {
            getLogger().warning("[Souls] Command 'souls' not found in plugin.yml!");
        }

        // Register event listeners
        getServer().getPluginManager().registerEvents(new PlayerKillListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(killsConfig), this); // Ensures player names update

        // Load additional configuration file (kills.yml)
        loadKillsConfig();

        // Ensure the hologram leaderboard exists or create it if missing
        SoulsHologramManager.ensureHologramExists();

        // Update the hologram for all players after plugin load
        updateHologramForAllPlayers();

        getLogger().info("[Souls] Plugin setup complete!");
    }


    @Override
    public void onDisable() {
        getLogger().info("[Souls] Plugin has been disabled!");
    }

    /**
     * Ensures the kills.yml file exists and is properly loaded.
     */
    private void loadKillsConfig() {
        if (killsConfig == null) {
            killsConfig = new KillsConfig(this); // Initialize the killsConfig if it's not already done
            getLogger().info("[Souls] Successfully loaded Kills config.");
        }
    }

    /**
     * Provides access to the kills configuration.
     *
     * @return the kills config
     */
    public KillsConfig getKillsConfig() {
        return killsConfig;
    }

    /**
     * Updates the hologram for all players with the latest soul counts.
     */
    private void updateHologramForAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            SoulsHologramManager.updateHologram(player);  // Update hologram for each player
        }
    }
}
