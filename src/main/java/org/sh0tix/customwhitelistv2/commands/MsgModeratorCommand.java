package org.sh0tix.customwhitelistv2.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MsgModeratorCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("[CustomWhitelistV2] Please provide a message");
            return true;
        }

        String message = String.join(" ", args);
        
        // Add the following präfix to the message: "[Message to the moderators] " in the color blue. The rest of the message should be in the color white
        message = "§a[Message to the moderators] §f" + message;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("customwhitelistv2.manage")) {
                player.sendMessage(message);
            }
        }

        return true;
    }
}
