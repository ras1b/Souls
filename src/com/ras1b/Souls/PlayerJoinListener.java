package com.ras1b.Souls;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

import com.ras1b.Souls.utils.KillsConfig;

public class PlayerJoinListener implements Listener {

    private final KillsConfig killsConfig;

    public PlayerJoinListener(KillsConfig killsConfig) {
        this.killsConfig = killsConfig;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        killsConfig.updatePlayerName(player);
    }
}
