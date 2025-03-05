package com.ras1b.Commands;

import com.ras1b.Souls.Souls;
import com.ras1b.Souls.utils.KillsConfig;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SoulsCommand implements CommandExecutor {

    private final Souls plugin;

    public SoulsCommand(Souls plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        KillsConfig killsConfig = plugin.getKillsConfig();

        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                int souls = killsConfig.getSouls(player.getUniqueId());
                player.sendMessage("§6You have " + souls + " Souls!");
            } else {
                sender.sendMessage("This command can only be used by players!");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
            if (!sender.hasPermission("souls.reload")) {
                sender.sendMessage("§cYou do not have permission to reload the plugin!");
                return true;
            }

            sender.sendMessage("§6[Souls] Reloading plugin...");
            plugin.reloadPlugin();
            sender.sendMessage("§a[Souls] Plugin successfully reloaded!");
            return true;
        }

        if (args.length == 1) {
            String targetName = args[0];
            @SuppressWarnings("deprecation")
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetName);

            if (targetPlayer == null) {
                sender.sendMessage("§cCould not find player " + targetName + "!");
                return true;
            }

            UUID targetUUID = targetPlayer.getUniqueId();
            int targetSouls = killsConfig.getSouls(targetUUID);

            sender.sendMessage("§6" + targetName + " has " + targetSouls + " Souls!");
            return true;
        }

        sender.sendMessage("§cUsage: /souls [player|reload|rl]");
        return true;
    }
}
