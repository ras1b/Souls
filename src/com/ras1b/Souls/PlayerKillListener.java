package com.ras1b.Souls;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.ras1b.Placeholders.SoulsPlaceholder;
import com.ras1b.Souls.utils.KillsConfig;

import me.clip.placeholderapi.PlaceholderAPI;

public class PlayerKillListener implements Listener {

    private final Souls plugin;
    private final KillsConfig killsConfig;

    public PlayerKillListener(Souls plugin) {
        this.plugin = plugin;
        this.killsConfig = plugin.getKillsConfig();
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null) {
            return;
        }

        // Fetch the soul reward from config.yml
        int soulsGained = plugin.getConfig().getInt("souls.per-kill", 5);
        killsConfig.addSouls(killer, soulsGained);

        // Get the updated soul count
        int newSoulCount = killsConfig.getSouls(killer.getUniqueId());

        // Notify the killer
        killer.sendMessage("§6You gained " + soulsGained + " Souls! Total Souls: " + newSoulCount);

        // **Live leaderboard update** (Async for better performance)
        Bukkit.getScheduler().runTaskAsynchronously(plugin, SoulsPlaceholder::updateTopSouls);

        // **Update the player's TAB list**
        updateTabList(killer, newSoulCount);
    }


    /**
     * Updates the player's tab list with their new soul count.
     */
    private void updateTabList(Player player, int newSoulCount) {
        final String formattedTabName = "§5⚔ §fꜱᴏᴜʟꜱ: §5" + newSoulCount;

        // Ensure PlaceholderAPI updates dynamically
        final String updatedTabName = PlaceholderAPI.setPlaceholders(player, formattedTabName);

        // Check if the TAB plugin is installed and update the player's name
        if (plugin.getServer().getPluginManager().getPlugin("TAB") != null) {
            Bukkit.getScheduler().runTask(plugin, () -> player.setPlayerListName(updatedTabName));
        }
    }

}
