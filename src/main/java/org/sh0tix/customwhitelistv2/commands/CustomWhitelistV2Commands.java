package org.sh0tix.customwhitelistv2.commands;

import com.google.common.base.CaseFormat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.sh0tix.customwhitelistv2.handlers.PasswordHandler;

public class CustomWhitelistV2Commands implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String string, @NotNull String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage("Please provide a subcommand");
            return true;
        }

        String subcommand = args[0];

        switch (subcommand) {
            case "reload":
                // Reload logic here
                commandSender.sendMessage("Reloaded the plugin");
                break;
                
            case "addPlayer":
                // Add logic here
                commandSender.sendMessage("Added a player to the custom whitelist list");
                break;
                
            case "removePlayer":
                // Remove logic here
                commandSender.sendMessage("Removed a player from the custom whitelist list");
                break;
                
            case "listPlayers":
                // List logic here
                commandSender.sendMessage("Listed the players in the custom whitelist");
                break;

            case "statusOfPlayer":
                // Status logic here
                commandSender.sendMessage("List the status of a player in the custom whitelist");
                break;

            case "updatePlayerStatus":
                // Update player logic here
                commandSender.sendMessage("Update the status of a player in the custom whitelist");
                break;

            case "updatePassword":
                // Update password logic here
                commandSender.sendMessage("Update the join password for the custom whitelist");
                String newPassword = args[1];
                boolean tryUpdatePassword = PasswordHandler.updatePassword(newPassword);
                if (tryUpdatePassword) {
                    commandSender.sendMessage("Password updated successfully");
                } else {
                    commandSender.sendMessage("Error updating the password");
                }
                break;

            case "checkPassword":
                // Help logic here
                commandSender.sendMessage("Check the password");
                String inputPassword = args[1];
                boolean tryCheckPassword = PasswordHandler.checkPassword(inputPassword);
                if (tryCheckPassword) {
                    commandSender.sendMessage("Password is correct");
                } else {
                    commandSender.sendMessage("Password is incorrect");
                }
                break;
                
            case "checkLoginFunctionality":
                // The same as the login command. This is just for testing purposes to check if the password is correct and to check if the user can login
                commandSender.sendMessage("Check the login functionality");
                
                
                break;

            case "help":
                // Help logic here
                commandSender.sendMessage("Help for the plugin");
                break;
                
            default:
                commandSender.sendMessage("Unknown subcommand");
                break;
        }

        return true;
    }
}
