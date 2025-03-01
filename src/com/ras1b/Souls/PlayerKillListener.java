package com.ras1b.Souls;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.ras1b.Placeholders.SoulsHologramManager;
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
            // Retrieve the kills configuration (assuming plugin provides it)
            KillsConfig killsConfig = plugin.getKillsConfig();

            // Increase the soul count for the killer. For example, let's say a player gains 5 souls per kill.
            int soulsGained = 5;
            killsConfig.addKill(killer, soulsGained);  // Now passing both UUID and souls gained

            // Get the updated soul count after the kill
            int newSoulCount = killsConfig.getSouls(killer.getUniqueId());

            // Inform the killer about the new soul count
            killer.sendMessage("§6You gained " + soulsGained + " Souls! Total Souls: " + newSoulCount);

            // Update the player's tab list with their new soul count
            updateTabList(killer, newSoulCount);

            // Update the hologram leaderboard for the killer to reflect the changes
            SoulsHologramManager.updateHologram(killer);

            // Update the hologram leaderboard for all online players
            SoulsHologramManager.updateHologramForAllPlayers();
        }
    }



    // Update the player's TAB list to reflect the latest soul count
    private void updateTabList(Player player, int newSoulCount) {
        // Use PlaceholderAPI to set the souls placeholder dynamically
        @SuppressWarnings("unused")
		String soulsPlaceholder = "%souls_count%"; // Placeholder for souls count
        String updatedTabName = "§5⚔ §fꜱᴏᴜʟꜱ: §5" + newSoulCount;

        // Ensure PlaceholderAPI is correctly hooked and the soul count is reflected
        updatedTabName = PlaceholderAPI.setPlaceholders(player, updatedTabName);

        // Check if the player has the TAB plugin installed and update their tab name
        if (plugin.getServer().getPluginManager().getPlugin("TAB") != null) {
            // Schedule the task to update the player's TAB list name
            final String finalUpdatedTabName = updatedTabName; // Make the variable final or effectively final
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                player.setPlayerListName(finalUpdatedTabName); // Update player's name in the TAB list
            });
        }
    }
}
