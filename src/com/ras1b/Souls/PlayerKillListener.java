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

    public PlayerKillListener(Souls plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null) {
            KillsConfig killsConfig = plugin.getKillsConfig();

            // Award souls on kill (adjust value as needed)
            int soulsGained = 5;
            killsConfig.addKill(killer, soulsGained);

            // Get the updated soul count
            int newSoulCount = killsConfig.getSouls(killer.getUniqueId());

            // Notify the killer
            killer.sendMessage("§6You gained " + soulsGained + " Souls! Total Souls: " + newSoulCount);

            // **Live leaderboard update**
            SoulsPlaceholder.updateTopSouls(); // Refresh top souls list

            // **Force PlaceholderAPI refresh after leaderboard update**
            Bukkit.getScheduler().runTaskLater(plugin, SoulsPlaceholder::updateTopSouls, 5L);

            // **Update the player's TAB list**
            updateTabList(killer, newSoulCount);
        }
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
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                player.setPlayerListName(updatedTabName);
            });
        }
    }
}
