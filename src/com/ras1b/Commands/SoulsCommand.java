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

        // If no arguments, show sender's own souls
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

        // If a player name is provided, fetch their souls
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

            if (targetSouls > 0) {
                sender.sendMessage("§6" + targetName + " has " + targetSouls + " Souls!");
            } else {
                sender.sendMessage("§6" + targetName + " has no Souls!");
            }
            return true;
        }

        // If too many arguments, show usage message
        sender.sendMessage("§cUsage: /souls [player]");
        return true;
    }
}
