package org.sh0tix.customwhitelistv2.commands;

import com.destroystokyo.paper.utils.PaperPluginLogger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.sh0tix.customwhitelistv2.handlers.PasswordHandler;
import org.sh0tix.customwhitelistv2.handlers.PlayerStatusHandler;
import org.sh0tix.customwhitelistv2.handlers.SendMessageToPlayer;
import org.sh0tix.customwhitelistv2.handlers.WhitelistHandler;
import org.sh0tix.customwhitelistv2.whitelist.CWV2Player;

import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

public class CustomWhitelistV2Commands implements CommandExecutor {
    
    private enum CommandStatus {
        ACTIVE,
        INACTIVE,
        UNDER_MAINTENANCE
    }
    
    private enum AllCommands {
        enableOrDisableSubCommand,
        listAllActivatedSubCommands,
        addPlayer,
        removePlayer,
        listPlayers,
        statusOfPlayer,
        updatePlayerStatus,
        updatePassword,
        checkPassword,
        checkLoginFunctionality,
        help
    }
    
    private final HashMap<AllCommands, CommandStatus> commandStatusHashMap = new HashMap<>();
    private final HashMap<String, AllCommands> commandDescriptionHashMap = new HashMap<>();
    
    public CustomWhitelistV2Commands() {
        initialiseCommandStatus();
        initialiseCommandMapping();
    }
    
    private void initialiseCommandMapping() {
        commandDescriptionHashMap.put("enableOrDisableSubCommand", AllCommands.enableOrDisableSubCommand);
        commandDescriptionHashMap.put("listAllActivatedSubCommands", AllCommands.listAllActivatedSubCommands);
        commandDescriptionHashMap.put("addPlayer", AllCommands.addPlayer);
        commandDescriptionHashMap.put("removePlayer", AllCommands.removePlayer);
        commandDescriptionHashMap.put("listPlayers", AllCommands.listPlayers);
        commandDescriptionHashMap.put("statusOfPlayer", AllCommands.statusOfPlayer);
        commandDescriptionHashMap.put("updatePlayerStatus", AllCommands.updatePlayerStatus);
        commandDescriptionHashMap.put("updatePassword", AllCommands.updatePassword);
        commandDescriptionHashMap.put("checkPassword", AllCommands.checkPassword);
        commandDescriptionHashMap.put("checkLoginFunctionality", AllCommands.checkLoginFunctionality);
        commandDescriptionHashMap.put("help", AllCommands.help);
    }
    
    private void initialiseCommandStatus() {
        for (AllCommands command : AllCommands.values()) {
            commandStatusHashMap.put(command, CommandStatus.INACTIVE);
        }
        
        // Set the commands that are active to active
        commandStatusHashMap.put(AllCommands.statusOfPlayer, CommandStatus.ACTIVE);
        commandStatusHashMap.put(AllCommands.updatePlayerStatus, CommandStatus.ACTIVE);
        commandStatusHashMap.put(AllCommands.removePlayer, CommandStatus.ACTIVE);
        commandStatusHashMap.put(AllCommands.updatePassword, CommandStatus.ACTIVE);
        commandStatusHashMap.put(AllCommands.checkPassword, CommandStatus.ACTIVE);
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
            case "enableOrDisableASubCommand":
                // Enable logic here
                // Get the subcommand from the args
                String subCommandToEnableOrDisable = args[1];
                
                // Get the status from the args
                String enableOrDisable = args[2];
                
                // Check if the sub command is a valid sub command
                if (!EnumUtils.isValidEnum(AllCommands.class, subCommandToEnableOrDisable)) {
                    commandSender.sendMessage("Unknown subcommand");
                    return true;
                }
                
                // Check if the enableOrDisable is a valid status
                if (!enableOrDisable.equalsIgnoreCase("enable") && !enableOrDisable.equalsIgnoreCase("disable")) {
                    commandSender.sendMessage("Unknown status");
                    return true;
                }
                
                // Get the sub command as an enum
                AllCommands subCommandAsEnum = EnumUtils.getEnum(AllCommands.class, subCommandToEnableOrDisable);
                
                // Set the status of the sub command
                if (enableOrDisable.equalsIgnoreCase("enable")) {
                    setCommandStatus(subCommandAsEnum, CommandStatus.ACTIVE);
                    commandSender.sendMessage("Enabled the subcommand");
                } else {
                    setCommandStatus(subCommandAsEnum, CommandStatus.INACTIVE);
                    commandSender.sendMessage("Disabled the subcommand");
                }
                
                break;

                
                
            case "listAllActivatedSubCommands":
                // List logic here
                SendMessageToPlayer message = new SendMessageToPlayer();
                message.addPartToMessage("The following subcommands are active:\n", Color.YELLOW);
                for (AllCommands commandEnum : AllCommands.values()) {
                    if (commandStatusHashMap.get(commandEnum) == CommandStatus.ACTIVE) {
                        message.addPartToMessage(commandEnum.toString() + "\n", Color.GREEN);
                    }
                }
                message.addPartToMessage("The following subcommands are inactive: ", Color.YELLOW);
                for (AllCommands commandEnum : AllCommands.values()) {
                    if (commandStatusHashMap.get(commandEnum) == CommandStatus.INACTIVE) {
                        message.addPartToMessage(commandEnum.toString() + "\n", Color.RED);
                    }
                }
                message.addPartToMessage("If you want to enable or disable a subcommand, use the command /customWhitelistV2 enableOrDisableASubCommand <subcommand> <enable/disable>", Color.YELLOW);
                commandSender.sendMessage(message.getMessage());
                
                break;
               
                
                
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
                
                // Get the player name from the args
                playerName = args[1];
                
                // Get the player UUID from the player name
                playerUuid = PlayerStatusHandler.getPlayerUuidFromName(playerName);
                
                // If the playerUuid is null, give the player a message that the player has not joined the server yet
                if (playerUuid == null) {
                    commandSender.sendMessage(Component.text("§cThe player §a" + playerName + "§c has not joined the server yet"));
                    return true;
                }
                
                // Get the player by its UUID
                CWV2Player playerToCheckStatus = PlayerStatusHandler.FindPlayerByUUID(playerUuid);
                
                // Send the command sender a message with the status of the player
                assert playerToCheckStatus != null;
                commandSender.sendMessage(Component.text("§aThe status of the player §c" + playerName + "§a is §c" + playerToCheckStatus.getStatus()));
                
                break;

                
                
            case "updatePlayerStatus":
                // Update player logic here
                commandSender.sendMessage("Update the status of a player in the custom whitelist");
                
                // Get the player name from the args
                String playerNameToUpdate = args[1];
                
                // Get the player UUID from the player name
                String playerUuidToUpdate = PlayerStatusHandler.getPlayerUuidFromName(playerNameToUpdate);
                
                // If the playerUuid is null, give the player a message that the player is not found
                if (playerUuidToUpdate == null) {
                    commandSender.sendMessage(Component.text("§cThe player §a" + playerNameToUpdate + "§c is not found"));
                    return true;
                }
                
                // Get the status from the args
                String status = args[2];
                
                // Check if the status is valid
                try {
                    CWV2Player.Status.valueOf(status);
                } catch (IllegalArgumentException e) {
                    commandSender.sendMessage(Component.text("§cThe status §a" + status + "§c is not valid"));
                    return true;
                }
                
                // Update the status of the player
                PlayerStatusHandler.updatePlayerStatus(playerUuidToUpdate, CWV2Player.Status.valueOf(status));
                
                // Send the command sender a message that the status of the player has been updated
                commandSender.sendMessage(Component.text("§aUpdated the status of the player §c" + playerNameToUpdate + "§a to §c" + status));
                
                // Check if the updated player is currently online
                Player playerOnline = Bukkit.getPlayer(playerNameToUpdate);
                
                if (playerOnline != null) {
                    // Send the player a message that their status has been updated
                    playerOnline.sendMessage(Component.text("§aYour status has been updated to §c" + status));
                }

                // Handle different updated statuses
                switch (CWV2Player.Status.valueOf(status)) {
                    case WHITELISTED:
                        // If the player is online, give the player all the effects a whitelisted player would have
                        if (playerOnline != null) {
                            WhitelistHandler.enablePlayerMovementAndSight(playerOnline);
                        }
                        break;
                    case NOT_WHITELISTED:
                        // If the player is online, give the player all the effects a non whitelisted player would have
                        if (playerOnline != null) {
                            WhitelistHandler.disablePlayerMovementAndSight(playerOnline);
                        }
                        break;
                    case BANNED, KICKED, TEMP_BANNED, TEMP_KICKED:
                        // If the player is online, give the player all the effects a banned player would have
                        if (playerOnline != null) {
                            WhitelistHandler.disablePlayerMovementAndSight(playerOnline);
                        }
                        break;
                    case REMOVED:
                        // If the player is online, give the player all the effects a removed player would have
                        if (playerOnline != null) {
                            WhitelistHandler.disablePlayerMovementAndSight(playerOnline);
                        }
                        break;
                    case UNKNOWN:
                        // This should never happen. Log an error message
                        PaperPluginLogger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe("The status of the player is unknown. This should never happen");
                        break;
                }
                
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
                commandSender.sendMessage("The subcommand + " + subcommand + " is not valid");
                break;
        }

        return true;
    }
}
