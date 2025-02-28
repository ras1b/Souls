package com.ras1b.Souls;

import com.ras1b.Souls.utils.KillsConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;

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
            killsConfig.addKill(killer.getUniqueId());
            killer.sendMessage("§6You gained 5 Souls! Total Souls: " + killsConfig.getSouls(killer.getUniqueId()));
        }
    }
}
