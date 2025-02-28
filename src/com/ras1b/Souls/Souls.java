package com.ras1b.Souls;

import com.ras1b.Commands.SoulsCommand;
import com.ras1b.Souls.utils.KillsConfig;
import org.bukkit.plugin.java.JavaPlugin;

public class Souls extends JavaPlugin {

    private KillsConfig killsConfig;

    @Override
    public void onEnable() {
        getLogger().info("[Souls] Plugin has been enabled!");

        // Load kills.yml configuration
        killsConfig = new KillsConfig(this);
        killsConfig.loadConfig();

        // Register commands
        getCommand("souls").setExecutor(new SoulsCommand(this));

        // Register event listeners
        getServer().getPluginManager().registerEvents(new PlayerKillListener(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("[Souls] Plugin has been disabled!");
    }

    public KillsConfig getKillsConfig() {
        return killsConfig;
    }
}
