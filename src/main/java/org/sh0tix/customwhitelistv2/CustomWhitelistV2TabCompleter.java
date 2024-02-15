package org.sh0tix.customwhitelistv2;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CustomWhitelistV2TabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String strings, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("customWhitelistV2") && args.length == 1) {
            return Arrays.asList("reload", "addPlayer", "removePlayer", "listPlayers", "statusOfPlayer", "updatePlayerStatus", "updatePassword", "checkPassword", "checkLoginFunctionality", "help");
        }
        
        return null;
    }
}
