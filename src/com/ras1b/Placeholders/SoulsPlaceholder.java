package com.ras1b.Placeholders;

import java.io.File;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class SoulsPlaceholder extends PlaceholderExpansion {

    private final Plugin plugin;
    private static final Map<String, Integer> topSouls = new LinkedHashMap<>();

    public SoulsPlaceholder(Plugin plugin) {
        this.plugin = plugin;
        updateTopSouls(); // Load leaderboard data when plugin starts
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
        if (player == null) return "0"; // If player is null, return 0

        // Return total soul count for the player
        if (identifier.equalsIgnoreCase("count")) {
            return String.valueOf(getSouls(player.getUniqueId()));
        }

        // Return player's position on the leaderboard
        if (identifier.equalsIgnoreCase("your_position")) {
            return getPlayerPosition(player);
        }

        // Return player's soul value
        if (identifier.equalsIgnoreCase("your_value")) {
            return String.valueOf(getSouls(player.getUniqueId()));
        }

        // Loop through placeholders for top 1-10 players
        for (int i = 1; i <= 10; i++) {
            if (identifier.equalsIgnoreCase("top_" + i + "_name")) {
                return getTopPlayerName(i);
            } else if (identifier.equalsIgnoreCase("top_" + i + "_value")) {
                return String.valueOf(getTopPlayerSouls(i));
            }
        }

        return null; // Return null if no matching placeholder
    }

    /**
     * Loads the top 10 players based on their souls from kills.yml.
     */
    public static void updateTopSouls() {
    File file = new File("plugins/Souls/kills.yml");
    if (!file.exists()) return;

    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
    Map<String, Integer> soulsMap = new HashMap<>();

    // Load all players from kills.yml
    for (String uuid : config.getKeys(false)) {
        int souls = config.getInt(uuid + ".souls", 0);
        String playerName = config.getString(uuid + ".name", "Unknown");
        soulsMap.put(playerName, souls);
    }

    // Ensure leaderboard always has 10 players
    synchronized (topSouls) {
        topSouls.clear();
        LinkedHashMap<String, Integer> sorted = sortByValueDescending(soulsMap);

        int count = 0;
        for (Map.Entry<String, Integer> entry : sorted.entrySet()) {
            topSouls.put(entry.getKey(), entry.getValue());
            count++;
            if (count >= 10) break; // Stop at 10 players
        }

        // Fill remaining spots with placeholders if needed
        while (topSouls.size() < 10) {
            topSouls.put("---", 0);
        }
    }

    // **Force PlaceholderAPI to refresh and update Hologram**
    Bukkit.getScheduler().runTaskLaterAsynchronously(Bukkit.getPluginManager().getPlugin("Souls"), () -> {
        Bukkit.getLogger().info("[Souls] Leaderboard updated live!");

        // Refresh the hologram data if using DecentHolograms
        Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("Souls"), () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "holograms reload");
        });

    }, 5L);
}

    /**
     * Sorts the players by souls in descending order.
     */
    private static LinkedHashMap<String, Integer> sortByValueDescending(Map<String, Integer> unsortedMap) {
        return unsortedMap.entrySet()
                .stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue())) // Descending order
                .collect(LinkedHashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()), LinkedHashMap::putAll);
    }

    /**
     * Gets the player's position in the leaderboard.
     */
    private String getPlayerPosition(OfflinePlayer player) {
        synchronized (topSouls) {
            List<String> sortedPlayers = new ArrayList<>(topSouls.keySet());
            int position = sortedPlayers.indexOf(player.getName()) + 1;
            return position > 0 ? String.valueOf(position) : "--"; // Show "--" if not in top 10
        }
    }

    /**
     * Fetches a player's souls count from kills.yml.
     */
    private int getSouls(UUID uuid) {
        File file = new File(plugin.getDataFolder(), "kills.yml");
        if (!file.exists()) return 0;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config.getInt(uuid.toString() + ".souls", 0);
    }

    /**
     * Gets the name of the top player at a specific rank.
     */
    private String getTopPlayerName(int rank) {
        synchronized (topSouls) {
            List<String> sortedPlayers = new ArrayList<>(topSouls.keySet());
            return (rank <= sortedPlayers.size()) ? sortedPlayers.get(rank - 1) : "---"; // Default "---" if no player
        }
    }

    /**
     * Gets the soul count of the top player at a specific rank.
     */
    private int getTopPlayerSouls(int rank) {
        synchronized (topSouls) {
            List<Integer> sortedSouls = new ArrayList<>(topSouls.values());
            return (rank <= sortedSouls.size()) ? sortedSouls.get(rank - 1) : 0; // Default 0 if no data
        }
    }
}
