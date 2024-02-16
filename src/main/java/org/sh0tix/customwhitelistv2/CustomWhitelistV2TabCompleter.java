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
            return Arrays.asList("enableOrDisableASubCommand", "listAllActivatedSubCommands", "addPlayer", "removePlayer", "listPlayers", "statusOfPlayer", "updatePlayerStatus", "updatePassword", "checkPassword", "checkLoginFunctionality", "help");
        }

        // If the player chooses the sub command "updatePlayerStatus", we want to provide the player with the following options:
        //        WHITELISTED
        //        NOT_WHITELISTED,
        //        BANNED,
        //        KICKED,
        //        TEMP_BANNED,
        //        TEMP_KICKED,
        //        REMOVED,
        //        UNKNOWN
        if (command.getName().equalsIgnoreCase("customWhitelistV2") && args.length == 3 && args[0].equalsIgnoreCase("updatePlayerStatus")) {
            return Arrays.asList("WHITELISTED", "NOT_WHITELISTED", "BANNED", "KICKED", "TEMP_BANNED", "TEMP_KICKED", "REMOVED", "UNKNOWN");
        }
        
        // If the player wants to enable or disable a sub command, we want to provide the player with the following options:
        //        ADD_PLAYER,
        //        REMOVE_PLAYER,
        //        LIST_PLAYERS,
        //        STATUS_OF_PLAYER,
        //        UPDATE_PLAYER_STATUS,
        //        UPDATE_PASSWORD,
        //        CHECK_PASSWORD,
        //        CHECK_LOGIN_FUNCTIONALITY,
        //        HELP
        if (command.getName().equalsIgnoreCase("customWhitelistV2") && args.length == 2 && args[0].equalsIgnoreCase("enableOrDisableASubCommand")) {
            return Arrays.asList("addPlayer", "removePlayer", "listPlayers", "statusOfPlayer", "updatePlayerStatus", "updatePassword", "checkPassword", "checkLoginFunctionality", "help");
        } else if (command.getName().equalsIgnoreCase("customWhitelistV2") && args.length == 3 && args[0].equalsIgnoreCase("enableOrDisableASubCommand")) {
            return Arrays.asList("enable", "disable");
        }
        
        return null;
    }
}
