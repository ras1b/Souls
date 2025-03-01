package com.ras1b.Placeholders;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;

public class SoulsHologramManager {
    private static final String HOLOGRAM_NAME = "top_souls";
    
    // File references for kills and topsouls
    private static final File killsFile = new File("plugins/Souls/kills.yml");
    private static final FileConfiguration killsConfig = YamlConfiguration.loadConfiguration(killsFile);
    private static final File topsoulsFile = new File("plugins/DecentHolograms/holograms/topsouls.yml");
    private static FileConfiguration topsoulsConfig = YamlConfiguration.loadConfiguration(topsoulsFile);

    // Default hologram location; configurable from topsouls.yml later
    private static Location hologramLocation = new Location(Bukkit.getWorld("world"), 0, 80, 0);

    // Default settings for hologram visibility and behavior
    private static boolean hologramEnabled = true;
    private static int displayRange = 48;
    private static int updateRange = 48;
    private static int updateInterval = 20;
    private static double facing = 0.0;
    private static boolean downOrigin = false;

    // Load configuration for hologram setup (location, enabled, etc.)
    static {
        if (!topsoulsFile.exists()) {
            Bukkit.getLogger().info("[Souls] topsouls.yml not found. Creating it...");
            createTopsoulsFile(); // Create the file if it doesn't exist
        }

        // Load values from topsouls.yml
        String worldName = topsoulsConfig.getString("location.world", "world");
        double x = topsoulsConfig.getDouble("location.x", 0);
        double y = topsoulsConfig.getDouble("location.y", 80);
        double z = topsoulsConfig.getDouble("location.z", 0);
        hologramLocation = new Location(Bukkit.getWorld(worldName), x, y, z);

        hologramEnabled = topsoulsConfig.getBoolean("enabled", true);
        displayRange = topsoulsConfig.getInt("display-range", 48);
        updateRange = topsoulsConfig.getInt("update-range", 48);
        updateInterval = topsoulsConfig.getInt("update-interval", 20);
        facing = topsoulsConfig.getDouble("facing", 0.0);
        downOrigin = topsoulsConfig.getBoolean("down-origin", false);
    }

    // Method to create the topsouls.yml file with default values if it doesn't exist
    private static void createTopsoulsFile() {
        try {
            if (topsoulsFile.createNewFile()) {
                Bukkit.getLogger().info("[Souls] topsouls.yml has been created.");
            } else {
                Bukkit.getLogger().info("[Souls] topsouls.yml already exists.");
            }

            // Set default values for topsouls.yml
            topsoulsConfig.set("location.world", "world");
            topsoulsConfig.set("location.x", 0);
            topsoulsConfig.set("location.y", 87.000);
            topsoulsConfig.set("location.z", 0);
            topsoulsConfig.set("enabled", true);
            topsoulsConfig.set("display-range", 48);
            topsoulsConfig.set("update-range", 48);
            topsoulsConfig.set("update-interval", 20);
            topsoulsConfig.set("facing", 0.0);
            topsoulsConfig.set("down-origin", false);

            // Save the configuration
            saveTopsoulsConfig();
        } catch (IOException e) {
            Bukkit.getLogger().severe("[Souls] Could not create topsouls.yml!");
            e.printStackTrace();
        }
    }

    // Method to save the topsouls.yml configuration
    private static void saveTopsoulsConfig() {
        try {
            topsoulsConfig.save(topsoulsFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe("[Souls] Could not save topsouls.yml!");
            e.printStackTrace();
        }
    }

    public static void updateHologram(Player player) {

        if (!killsFile.exists()) {
            Bukkit.getLogger().warning("[Souls] kills.yml file not found! Hologram update aborted.");
            return;
        }

        Bukkit.getLogger().info("[Souls] Updating hologram...");

        Map<String, Integer> soulsMap = new HashMap<>();
        // Fetch the players' soul data
        for (String uuid : killsConfig.getKeys(false)) {
            int souls = killsConfig.getInt(uuid + ".souls", 0);
            soulsMap.put(uuid, souls);
        }

        // Sort players by their soul count in descending order, then by kills as a tiebreaker
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(soulsMap.entrySet());
        sortedList.sort((a, b) -> {
            int soulCompare = Integer.compare(b.getValue(), a.getValue());
            if (soulCompare == 0) {
                // If souls are equal, use kills as a tiebreaker
                int killsA = killsConfig.getInt(a.getKey() + ".kills", 0);
                int killsB = killsConfig.getInt(b.getKey() + ".kills", 0);
                return Integer.compare(killsB, killsA); // Higher kills first
            }
            return soulCompare; // Otherwise, sort by souls
        });

        // Debugging: log the sorted list
        Bukkit.getLogger().info("[Souls] Sorted Souls List: " + sortedList);

        List<String> hologramLines = new ArrayList<>();
        hologramLines.add(" &#FDE49E👻 &#FEB941&lᴛᴏᴘ &f| &#FEB941&lꜱᴏᴜʟꜱ &#FDE49E👻");

        int position = 1;
        String playerUUID = player != null ? player.getUniqueId().toString() : "UnknownUUID";
        int playerSouls = soulsMap.getOrDefault(playerUUID, 0);
        int playerPosition = -1;

        // Iterate over the sorted list and display the top 10 players
        for (Map.Entry<String, Integer> entry : sortedList) {
            if (position <= 10) {
                // Fetch the player's name from kills.yml instead of topsouls.yml
                String playerDisplayName = killsConfig.getString(entry.getKey() + ".name");
                if (playerDisplayName == null || playerDisplayName.isEmpty()) {
                    playerDisplayName = entry.getKey();  // Fall back to UUID if name is missing
                }

                hologramLines.add("&#FEB941#" + position + " &f" + playerDisplayName + " &a→ &#FDE49E" + entry.getValue() + " 👻");
            }

            // Check if the current entry is the player in question
            if (entry.getKey().equals(playerUUID)) {
                playerPosition = position;
            }

            position++;
        }

        // Debugging: Log the player's position
        Bukkit.getLogger().info("[Souls] Player " + player.getName() + "'s position: " + playerPosition);

        hologramLines.add("&f"); // Add a line break
        hologramLines.add("&#FEB941• ʏᴏᴜʀ ᴘᴏꜱɪᴛɪᴏɴ:");

        // Show the player's own position correctly in the "Your Position" line
        if (playerPosition != -1) {
            // Correctly display the player's own position and soul count in the hologram
            hologramLines.add("&#FDE49E#" + playerPosition + " &f" + player.getName() + " &a→ &#FDE49E" + playerSouls + " 👻");
        } else {
            // If the player doesn't exist in the top 10, display it as not in the top
            hologramLines.add("&#FDE49E#-- &f" + player.getName() + " &a→ &#FDE49E" + playerSouls + " 👻");
        }

        // Create or update the hologram with the lines we generated
        createOrUpdateHologram(hologramLines);
    }


    // Method to create or update the hologram
    private static void createOrUpdateHologram(List<String> lines) {
        if (!hologramEnabled) {
            Bukkit.getLogger().info("[Souls] Hologram is disabled in configuration.");
            return; // Do nothing if hologram is disabled
        }

        Hologram hologram = DHAPI.getHologram(HOLOGRAM_NAME);

        if (hologram == null) {
            Bukkit.getLogger().info("[Souls] Hologram not found, creating a new one...");
            hologram = DHAPI.createHologram(HOLOGRAM_NAME, hologramLocation, false, lines);

            if (hologram != null) {
                Bukkit.getLogger().info("[Souls] Hologram '" + HOLOGRAM_NAME + "' created successfully at " + hologramLocation);
            } else {
                Bukkit.getLogger().severe("[Souls] Failed to create hologram!");
                return;
            }
        } else {
            Bukkit.getLogger().info("[Souls] Hologram found! Updating lines...");
            DHAPI.setHologramLines(hologram, lines);
        }

        Bukkit.getLogger().info("[Souls] Hologram '" + HOLOGRAM_NAME + "' updated successfully.");
    }

    // Method to ensure that the hologram exists and is ready
    public static void ensureHologramExists() {
        Hologram hologram = DHAPI.getHologram(HOLOGRAM_NAME);

        if (hologram == null) {
            Bukkit.getLogger().info("[Souls] Hologram not found, creating a new one...");
            List<String> initialLines = new ArrayList<>();
            initialLines.add(" &#FDE49E👻 &#FEB941&lᴛᴏᴘ &f| &#FEB941&lꜱᴏᴜʟꜱ &#FDE49E👻");
            initialLines.add("§7No data available.");
            hologram = DHAPI.createHologram(HOLOGRAM_NAME, hologramLocation, false, initialLines);
            Bukkit.getLogger().info("[Souls] Hologram created at " + hologramLocation);
        } else {
            Bukkit.getLogger().info("[Souls] Hologram already exists. Loading...");
        }
    }

    // Method to update the hologram for all players
    public static void updateHologramForAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateHologram(player); // Call the updateHologram method for each player
        }
    }
}
