package org.sh0tix.customwhitelistv2;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomWhitelistV2AdminTabCompleter implements TabCompleter {


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String strings, @NotNull String[] args) {
        // Debug command enable or disable
        if ((command.getName().equalsIgnoreCase("customWhitelistV2Admin") || command.getName().equalsIgnoreCase("cwv2a")) && args.length == 1) {
            String input = args[0].toLowerCase();
            List<String> allOptions = Arrays.asList("debug", "addModerator" , "removeModerator", "listAllModerators", "setPluginLanguage");
            allOptions.sort(String::compareToIgnoreCase);
            return allOptions.stream()
                    .filter(option -> option.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }

        if ((command.getName().equalsIgnoreCase("customWhitelistV2Admin") || command.getName().equalsIgnoreCase("cwv2a")) && args.length == 2 && args[0].equalsIgnoreCase("debug")) {
            String input = args[1].toLowerCase();
            List<String> allOptions = Arrays.asList("true", "false");
            allOptions.sort(String::compareToIgnoreCase);
            return allOptions.stream()
                    .filter(option -> option.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }

        if ((command.getName().equalsIgnoreCase("customWhitelistV2Admin") || command.getName().equalsIgnoreCase("cwv2a")) && args.length == 2 && args[0].equalsIgnoreCase("setPluginLanguage")) {
            String input = args[1].toLowerCase();
            List<String> allOptions = Arrays.asList("de_DE", "en_US");
            allOptions.sort(String::compareToIgnoreCase);
            return allOptions.stream()
                    .filter(option -> option.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }
        
        return null;
    }
}
