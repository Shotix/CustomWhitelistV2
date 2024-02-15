package org.sh0tix.customwhitelistv2.commands;

import com.google.common.base.CaseFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sh0tix.customwhitelistv2.handlers.PasswordHandler;
import org.sh0tix.customwhitelistv2.handlers.PlayerStatusHandler;
import org.sh0tix.customwhitelistv2.handlers.SendMessageToPlayer;
import org.sh0tix.customwhitelistv2.handlers.WhitelistHandler;
import org.sh0tix.customwhitelistv2.whitelist.CWV2Player;

import java.util.HashMap;
import java.util.Objects;

public class CustomWhitelistV2Commands implements CommandExecutor {
    
    private enum CommandStatus {
        ACTIVE,
        INACTIVE,
        UNDER_MAINTENANCE
    }
    
    private enum AllCommands {
        ADD_PLAYER,
        REMOVE_PLAYER,
        LIST_PLAYERS,
        STATUS_OF_PLAYER,
        UPDATE_PLAYER_STATUS,
        UPDATE_PASSWORD,
        CHECK_PASSWORD,
        CHECK_LOGIN_FUNCTIONALITY,
        HELP
    }
    
    private HashMap<AllCommands, CommandStatus> commandStatusHashMap = new HashMap<>();
    private HashMap<String, AllCommands> commandDescriptionHashMap = new HashMap<>();
    
    public CustomWhitelistV2Commands() {
        initialiseCommandStatus();
        initialiseCommandMapping();
    }
    
    private void initialiseCommandMapping() {
        commandDescriptionHashMap.put("addPlayer", AllCommands.ADD_PLAYER);
        commandDescriptionHashMap.put("removePlayer", AllCommands.REMOVE_PLAYER);
        commandDescriptionHashMap.put("listPlayers", AllCommands.LIST_PLAYERS);
        commandDescriptionHashMap.put("statusOfPlayer", AllCommands.STATUS_OF_PLAYER);
        commandDescriptionHashMap.put("updatePlayerStatus", AllCommands.UPDATE_PLAYER_STATUS);
        commandDescriptionHashMap.put("updatePassword", AllCommands.UPDATE_PASSWORD);
        commandDescriptionHashMap.put("checkPassword", AllCommands.CHECK_PASSWORD);
        commandDescriptionHashMap.put("checkLoginFunctionality", AllCommands.CHECK_LOGIN_FUNCTIONALITY);
        commandDescriptionHashMap.put("help", AllCommands.HELP);
    }
    
    private void initialiseCommandStatus() {
        for (AllCommands command : AllCommands.values()) {
            commandStatusHashMap.put(command, CommandStatus.INACTIVE);
        }
        
        // Set the commands that are active to active
        commandStatusHashMap.put(AllCommands.REMOVE_PLAYER, CommandStatus.ACTIVE);
        commandStatusHashMap.put(AllCommands.UPDATE_PASSWORD, CommandStatus.ACTIVE);
        commandStatusHashMap.put(AllCommands.CHECK_PASSWORD, CommandStatus.ACTIVE);
    }
    
    private void setCommandStatus(AllCommands command, CommandStatus status) {
        commandStatusHashMap.put(command, status);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String string, @NotNull String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage("Please provide a subcommand");
            return true;
        }

        String subcommand = args[0];
        
        AllCommands commandAsEnum = commandDescriptionHashMap.get(subcommand);
        
        // Check if the status of the command is active
        if (commandStatusHashMap.get(commandAsEnum) == CommandStatus.INACTIVE) {
            // Red message
            commandSender.sendMessage(Component.text("§cThis command is currently inactive"));
            return true;
        }

        switch (subcommand) {
            case "addPlayer":
                // Add logic here
                commandSender.sendMessage("Added a player to the custom whitelist list");
                break;
                
            case "removePlayer":
                // Remove logic here
                // Get the player name from the args
                String playerName = args[1];
                
                // Get the player UUID from the player name
                String playerUuid = PlayerStatusHandler.getPlayerUuidFromName(playerName);
                
                // If the playerUuid is null, give the player a message that the player is not found
                if (playerUuid == null) {
                    commandSender.sendMessage(Component.text("§cThe player §a" + playerName + "§c is not found"));
                    return true;
                }

                PlayerStatusHandler.updatePlayerStatus(playerUuid, CWV2Player.Status.NOT_WHITELISTED);
                
                commandSender.sendMessage(Component.text("§aRemoved the player §c" + playerName + "§a from the custom whitelist"));
                
                // Check if the removed player is currently online
                Player player = Bukkit.getPlayer(playerName);
                if (player != null) {
                    // Edit the status of the player to removed from the custom whitelist
                    PlayerStatusHandler.updatePlayerStatus(playerUuid, CWV2Player.Status.REMOVED);
                    
                    // Give the user all the effects a non whitelisted player would have
                    WhitelistHandler.disablePlayerMovementAndSight(player);
                    
                    // Send the player a message that they have been removed from the whitelist and that they can no longer join the server
                    Component kickMessage = Component.text()
                            .append(Component.text("You have been removed from the custom whitelist by a moderator. ", NamedTextColor.RED, TextDecoration.BOLD))
                            .append(Component.text("You can no longer play on the server. ", NamedTextColor.RED, TextDecoration.BOLD))
                            .append(Component.text("If you think this is a mistake, ", NamedTextColor.RED, TextDecoration.BOLD))
                            .append(Component.text("click here", NamedTextColor.BLUE, TextDecoration.UNDERLINED)
                                    .hoverEvent(HoverEvent.showText(Component.text("Click to send a help requests to the moderators", NamedTextColor.GREEN)))
                                    .clickEvent(ClickEvent.runCommand("/mm " + Objects.requireNonNull(commandSender).getName() + " needs help with the custom whitelist. Please help them.")))
                            .append(Component.text(" or contact an administrator.", NamedTextColor.RED, TextDecoration.BOLD))
                            .append(Component.text("\nIf you want to write a custom message to the moderators, ", NamedTextColor.RED, TextDecoration.BOLD))
                            .append(Component.text("you can use the command ", NamedTextColor.RED, TextDecoration.BOLD))
                            .append(Component.text("/msgModerator <message>", NamedTextColor.BLUE, TextDecoration.UNDERLINED))
                            .build();

                    player.sendMessage(kickMessage);
                }
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
