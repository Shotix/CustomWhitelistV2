package org.sh0tix.customwhitelistv2;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomWhitelistV2TabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String strings, @NotNull String[] args) {
        if ((command.getName().equalsIgnoreCase("customWhitelistV2") || command.getName().equalsIgnoreCase("cwv2")) && args.length == 1) {
            String input = args[0].toLowerCase();
            List<String> allSubCommands = Arrays.asList("enableOrDisableASubCommand", "listAllActivatedSubCommands", "addPlayer", "removePlayer", "listPlayers", "statusOfPlayer", "updatePlayerStatus", "updatePassword", "checkPassword", "help");
            allSubCommands.sort(String::compareToIgnoreCase);
            return allSubCommands.stream()
                    .filter(subCommand -> subCommand.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
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
        if ((command.getName().equalsIgnoreCase("customWhitelistV2") || command.getName().equalsIgnoreCase("cwv2")) && args.length == 3 && args[0].equalsIgnoreCase("updatePlayerStatus")) {
            String input = args[2].toLowerCase();
            List<String> allStatuses = Arrays.asList("WHITELISTED", "NOT_WHITELISTED", "BANNED", "KICKED", "TEMP_BANNED", "TEMP_KICKED", "REMOVED", "UNKNOWN");
            allStatuses.sort(String::compareToIgnoreCase);
            return allStatuses.stream()
                    .filter(status -> status.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }

        // If the player chooses the sub command "TEMP_BANNED" or "TEMP_KICKED", the player should now input a time period for the ban or kick.
        // This should be a number followed by a time unit (s, m, h, d, w, m, y).
        // The user should get a help message: <time period> (e.g. 1d, 2w, 3m, 4y)
        if ((command.getName().equalsIgnoreCase("customWhitelistV2") || command.getName().equalsIgnoreCase("cwv2")) && args.length == 4 && (args[2].equalsIgnoreCase("TEMP_BANNED") || args[2].equalsIgnoreCase("TEMP_KICKED"))) {
            return List.of("<time period> (e.g. 1d, 2w, 3m, 4y)");
        }

        // If the player chooses the sub command "TEMP_BANNED" or "TEMP_KICKED", the player should now input a reason for the ban or kick.
        // The user should get the following help message: <reason>
        if ((command.getName().equalsIgnoreCase("customWhitelistV2") || command.getName().equalsIgnoreCase("cwv2")) && args.length == 5 && (args[2].equalsIgnoreCase("TEMP_BANNED") || args[2].equalsIgnoreCase("TEMP_KICKED"))) {
            return List.of("<reason>");
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
        if ((command.getName().equalsIgnoreCase("customWhitelistV2") || command.getName().equalsIgnoreCase("cwv2")) && args.length == 2 && args[0].equalsIgnoreCase("enableOrDisableASubCommand")) {
            String input = args[1].toLowerCase();
            List<String> allSubCommands = Arrays.asList("addPlayer", "removePlayer", "listPlayers", "statusOfPlayer", "updatePlayerStatus", "updatePassword", "checkPassword", "checkLoginFunctionality", "help");
            allSubCommands.sort(String::compareToIgnoreCase);
            return allSubCommands.stream()
                    .filter(subCommand -> subCommand.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        } else if (command.getName().equalsIgnoreCase("customWhitelistV2") && args.length == 3 && args[0].equalsIgnoreCase("enableOrDisableASubCommand")) {
            String input = args[2].toLowerCase();
            List<String> allOptions = Arrays.asList("enable", "disable");
            allOptions.sort(String::compareToIgnoreCase);
            return allOptions.stream()
                    .filter(option -> option.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }
        return null;
    }
}
