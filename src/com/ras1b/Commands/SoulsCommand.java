package com.ras1b.Commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ras1b.Souls.Souls;
import com.ras1b.Souls.utils.KillsConfig;

public class SoulsCommand implements CommandExecutor {

    private final Souls plugin;
    private final KillsConfig killsConfig;

    public SoulsCommand(Souls plugin) {
        this.plugin = plugin;
        this.killsConfig = plugin.getKillsConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                player.sendMessage("§6You have " + killsConfig.getSouls(player.getUniqueId()) + " Souls!");
            } else {
                sender.sendMessage("This command can only be used by players!");
            }
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload", "rl" -> {
                if (!sender.hasPermission("souls.reload")) {
                    sender.sendMessage("§cYou do not have permission to reload the plugin!");
                    return true;
                }
                sender.sendMessage("§6[Souls] Reloading plugin...");
                plugin.reloadPlugin();
                plugin.getKillsConfig().reload(); // Ensure kills.yml is reloaded
                sender.sendMessage("§a[Souls] Plugin successfully reloaded!");
                return true;
            }
            default -> {
                @SuppressWarnings("deprecation")
				OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[0]);
                if (targetPlayer.hasPlayedBefore() || targetPlayer.isOnline()) {
                    sender.sendMessage("§6" + args[0] + " has " + killsConfig.getSouls(targetPlayer.getUniqueId()) + " Souls!");
                } else {
                    sender.sendMessage("§cCould not find player " + args[0] + "!");
                }
                return true;
            }
        }
    }
}
