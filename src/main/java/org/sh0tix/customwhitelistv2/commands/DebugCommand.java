package org.sh0tix.customwhitelistv2.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.sh0tix.customwhitelistv2.CustomWhitelistV2;

public class DebugCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String string, @NotNull String[] args) {
        if (args.length == 0) {
            return false;
        }
        
        if (args[0].equalsIgnoreCase("enable")) {
            // Enable debug mode
            CustomWhitelistV2.debugMode = true;
            
            // Write a message to everyone with the permission "customwhitelistv2.administrator" that the debug mode has been enabled by the player who executed the command
            CustomWhitelistV2.getInstance().getServer().getOnlinePlayers().stream()
                    .filter(player -> player.hasPermission("customwhitelistv2.administrator"))
                    .forEach(player -> player.sendMessage(Component.text()
                            .append(Component.text("[CustomWhitelistV2] WARNING: DEBUG MODE ENABLED BY " + commandSender.getName(), NamedTextColor.RED)
                                    .decoration(TextDecoration.BOLD, true))
                    ));
            
            return true;
        } else if (args[0].equalsIgnoreCase("disable")) {
            // Disable debug mode
            // Write a message to everyone with the permission "customwhitelistv2.administrator" that the debug mode has been enabled by the player who executed the command
            CustomWhitelistV2.getInstance().getServer().getOnlinePlayers().stream()
                    .filter(player -> player.hasPermission("customwhitelistv2.administrator"))
                    .forEach(player -> player.sendMessage(Component.text()
                            .append(Component.text("[CustomWhitelistV2] WARNING: DEBUG MODE DISABLED BY " + commandSender.getName(), NamedTextColor.GREEN)
                                    .decoration(TextDecoration.BOLD, true))
                    ));
            return true;
        }
        
        return false;
    }
}
