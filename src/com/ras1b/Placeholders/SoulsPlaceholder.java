package com.ras1b.Placeholders;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class SoulsPlaceholder extends PlaceholderExpansion {

    private final Plugin plugin;
    private static final Map<String, Integer> topSouls = new ConcurrentHashMap<>();
    private static final String HOLOGRAM_NAME = "topsouls";

    public SoulsPlaceholder(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, SoulsPlaceholder::updateTopSouls); // Async load
    }

    @Override
    public String getIdentifier() {
        return "souls";
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
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (player == null) return "0";

        // Fetch souls directly instead of relying on cached leaderboard
        File file = new File(plugin.getDataFolder(), "kills.yml");
        if (!file.exists()) return "0";

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        UUID playerUUID = player.getUniqueId();
        int souls = config.getInt(playerUUID.toString() + ".souls", 0);

        return switch (identifier.toLowerCase()) {
            case "count", "your_value" -> String.valueOf(souls);
            case "your_position" -> getPlayerPosition(player);
            default -> {
                for (int i = 1; i <= 10; i++) {
                    if (identifier.equalsIgnoreCase("top_" + i + "_name"))
                        yield getTopPlayerName(i);
                    if (identifier.equalsIgnoreCase("top_" + i + "_value"))
                        yield String.valueOf(getTopPlayerSouls(i));
                }
                yield null;
            }
        };
    }

    public static void updateTopSouls() {
        Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("Souls"), () -> {
            File file = new File("plugins/Souls/kills.yml");
            if (!file.exists()) return;

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            Map<String, Integer> newTopSouls = new HashMap<>();

            for (String uuid : config.getKeys(false)) {
                int souls = config.getInt(uuid + ".souls", 0);
                String playerName = config.getString(uuid + ".name", "Unknown");
                newTopSouls.put(playerName, souls);
            }

            LinkedHashMap<String, Integer> sorted = sortByValueDescending(newTopSouls);

            if (!sorted.equals(topSouls)) {
                synchronized (topSouls) {
                    topSouls.clear();
                    sorted.entrySet().stream().limit(10).forEach(entry -> topSouls.put(entry.getKey(), entry.getValue()));
                    while (topSouls.size() < 10) topSouls.put("---", 0);
                }
                Bukkit.getLogger().info("[Souls] Leaderboard updated.");
                refreshHologram();
            }
        });
    }

    private static void refreshHologram() {
        Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("DecentHolograms"), () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "holograms refresh " + HOLOGRAM_NAME);
        });
    }

    private static LinkedHashMap<String, Integer> sortByValueDescending(Map<String, Integer> unsortedMap) {
        return unsortedMap.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .collect(LinkedHashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()), LinkedHashMap::putAll);
    }

    private String getPlayerPosition(OfflinePlayer player) {
        String playerName = player.getName();
        if (playerName == null) return "--";

        synchronized (topSouls) {
            List<Map.Entry<String, Integer>> sortedPlayers = new ArrayList<>(topSouls.entrySet());
            sortedPlayers.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

            int position = 1;
            for (Map.Entry<String, Integer> entry : sortedPlayers) {
                if (entry.getKey().equals(playerName)) {
                    return String.valueOf(position);
                }
                position++;
            }
            return "--";
        }
    }

    private String getTopPlayerName(int rank) {
        synchronized (topSouls) {
            return topSouls.keySet().stream().skip(rank - 1).findFirst().orElse("---");
        }
    }

    private int getTopPlayerSouls(int rank) {
        synchronized (topSouls) {
            return topSouls.values().stream().skip(rank - 1).findFirst().orElse(0);
        }
    }
}