package com.ras1b.Placeholders;

import java.io.File;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class SoulsPlaceholder extends PlaceholderExpansion {

    private final Plugin plugin;

    public SoulsPlaceholder(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "souls"; // Placeholder identifier
    }

    @Override
    public String getAuthor() {
        return "ras1b";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // Keep placeholder registered after reload
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (player == null || !player.hasPlayedBefore()) {
            return "0"; // If the player has never played, return 0
        }

        // Check for the souls_count placeholder request
        if (identifier.equalsIgnoreCase("count")) {
            return String.valueOf(getSouls(player.getUniqueId())); // Get the soul count from the file
        }

        return null; // Return null if no matching placeholder
    }

    private int getSouls(UUID uuid) {
        File file = new File(plugin.getDataFolder(), "kills.yml");

        // If the file doesn't exist or is invalid, return 0 (default soul count)
        if (!file.exists()) {
            plugin.getLogger().warning("The kills.yml file does not exist! Returning 0 souls for player " + uuid);
            return 0;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        // Check if the UUID path exists directly in the configuration
        if (!config.contains(uuid.toString())) {
            plugin.getLogger().warning("No souls found for player " + uuid + " in kills.yml. Returning 0 souls.");
            return 0;
        }

        // Return souls from config (if exists, otherwise defaults to 0)
        return config.getInt(uuid.toString() + ".souls", 0);
    }
}
