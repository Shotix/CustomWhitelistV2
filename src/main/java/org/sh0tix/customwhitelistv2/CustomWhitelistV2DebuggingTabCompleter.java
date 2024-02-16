package org.sh0tix.customwhitelistv2;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomWhitelistV2DebuggingTabCompleter implements TabCompleter {


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String strings, @NotNull String[] args) {
        // Debug command enable or disable
        if ((command.getName().equalsIgnoreCase("customWhitelistV2Debugging") || command.getName().equalsIgnoreCase("cwv2d")) && args.length == 1) {
            String input = args[0].toLowerCase();
            List<String> allOptions = Arrays.asList("enable", "disable");
            allOptions.sort(String::compareToIgnoreCase);
            return allOptions.stream()
                    .filter(option -> option.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }
        
        return null;
    }
}
