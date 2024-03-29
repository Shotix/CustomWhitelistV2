package org.sh0tix.customwhitelistv2.commands;

import com.destroystokyo.paper.utils.PaperPluginLogger;
import com.google.gson.Gson;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.sh0tix.customwhitelistv2.handlers.PasswordHandler;
import org.sh0tix.customwhitelistv2.handlers.PlayerStatusHandler;
import org.sh0tix.customwhitelistv2.handlers.WhitelistHandler;
import org.sh0tix.customwhitelistv2.whitelist.CWV2Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
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
            commandStatusHashMap.put(command, CommandStatus.ACTIVE);
        }
        
        // Set the commands that are inactive to inactive
        
    }
    
    private void setCommandStatus(AllCommands command, CommandStatus status) {
        commandStatusHashMap.put(command, status);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String string, @NotNull String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage("[CustomWhitelistV2] Please provide a subcommand");
            return true;
        }

        String subcommand = args[0];
        
        AllCommands commandAsEnum = commandDescriptionHashMap.get(subcommand);
        
        // Check if the status of the command is active
        if (commandStatusHashMap.get(commandAsEnum) == CommandStatus.INACTIVE) {
            // Red message
            commandSender.sendMessage(Component.text()
                    .append(Component.text("[CustomWhitelistV2] The subcommand ", NamedTextColor.RED))
                    .append(Component.text(subcommand, NamedTextColor.WHITE))
                    .append(Component.text(" is currently inactive", NamedTextColor.RED)));
            return true;
        }

        String playerName;
        String playerUuid;
        
        
        switch (subcommand) {
            case "enableOrDisableASubCommand":
                // Enable logic here
                // Get the subcommand from the args
                String subCommandToEnableOrDisable = args[1];
                
                // Get the status from the args
                String enableOrDisable = args[2];
                
                // Check if the sub command is a valid sub command
                if (!EnumUtils.isValidEnum(AllCommands.class, subCommandToEnableOrDisable)) {
                    commandSender.sendMessage("[CustomWhitelistV2] Unknown subcommand");
                    return true;
                }
                
                // Check if the enableOrDisable is a valid status
                if (!enableOrDisable.equalsIgnoreCase("enable") && !enableOrDisable.equalsIgnoreCase("disable")) {
                    commandSender.sendMessage("[CustomWhitelistV2] Unknown status");
                    return true;
                }
                
                // Get the sub command as an enum
                AllCommands subCommandAsEnum = EnumUtils.getEnum(AllCommands.class, subCommandToEnableOrDisable);
                
                // Set the status of the sub command
                if (enableOrDisable.equalsIgnoreCase("enable")) {
                    setCommandStatus(subCommandAsEnum, CommandStatus.ACTIVE);
                    commandSender.sendMessage(Component.text("[CustomWhitelistV2] Enabled the subcommand", NamedTextColor.GREEN));
                } else {
                    setCommandStatus(subCommandAsEnum, CommandStatus.INACTIVE);
                    commandSender.sendMessage(Component.text("[CustomWhitelistV2] Disabled the subcommand", NamedTextColor.RED));
                }
                
                break;

                
                
            case "listAllActivatedSubCommands":
                // List logic here
                ComponentBuilder<TextComponent, TextComponent.Builder> messageToPlayerSubcommand = Component.text()
                        .append(Component.text("\n[CustomWhitelistV2] The following subcommands are active:\n\n", NamedTextColor.YELLOW));
                for (AllCommands commandEnum : AllCommands.values()) {
                    if (commandStatusHashMap.get(commandEnum) == CommandStatus.ACTIVE) {
                        messageToPlayerSubcommand.append(Component.text(commandEnum.toString() + "\n", NamedTextColor.GREEN));
                    }
                }
                messageToPlayerSubcommand.append(Component.text("\n[CustomWhitelistV2] The following subcommands are inactive: ", NamedTextColor.YELLOW));
                for (AllCommands commandEnum : AllCommands.values()) {
                    if (commandStatusHashMap.get(commandEnum) == CommandStatus.INACTIVE) {
                        messageToPlayerSubcommand.append(Component.text(commandEnum.toString() + "\n\n", NamedTextColor.RED));
                    }
                }
                messageToPlayerSubcommand.append(Component.text("If you want to enable or disable a subcommand, use the command /customWhitelistV2 enableOrDisableASubCommand <subcommand> <enable/disable>", NamedTextColor.YELLOW));
                
                commandSender.sendMessage(messageToPlayerSubcommand.build());
                
                break;
               
                
                
            case "addPlayer":
                // Add logic here
                // Mojang API URL: https://api.ashcon.app/mojang/v2/user/<username>
                String apiUrl = "https://api.ashcon.app/mojang/v2/user/<username>";
                
                // Get the player name from the args
                playerName = args[1];
                
                // Join the API URL with the player name
                apiUrl = apiUrl.replace("<username>", playerName);
                
                try {
                    // Open a connection to the Mojang API
                    URL url = new URL(apiUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    
                    // Set the request method to GET
                    conn.setRequestMethod("GET");
                    
                    // Read the response from the Mojang API
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    // Parse the response from the Mojang API
                    Gson gson = new Gson();
                    CWV2Player playerToAdd = gson.fromJson(response.toString(), CWV2Player.class);
                    
                    // Get the inputted status of the new player from the args
                    String status = args[2];
                    CWV2Player.Status newPlayerStatus;
                     
                    if (status == null) {
                        status = "THIS STATUS IS INVALID";
                    }
                    
                    // Check if the status is valid
                    try {
                        newPlayerStatus = CWV2Player.Status.valueOf(status);
                    } catch (Exception e) {
                        newPlayerStatus = CWV2Player.Status.NOT_WHITELISTED;
                        commandSender.sendMessage(Component.text()
                                .append(Component.text("[CustomWhitelistV2] The status ", NamedTextColor.YELLOW))
                                .append(Component.text(status, NamedTextColor.WHITE))
                                .append(Component.text(" is not valid", NamedTextColor.YELLOW))
                                .append(Component.text("\nThe player status has been initialized to ", NamedTextColor.YELLOW))
                                .append(Component.text(CWV2Player.Status.NOT_WHITELISTED.toString(), NamedTextColor.WHITE)));
                    }
                    
                    // Set the rest of the playerToAdd object
                    playerToAdd.setStatus(newPlayerStatus);
                    playerToAdd.setLastUpdated(CWV2Player.getUpdatedTime());
                    playerToAdd.setNumberOfTimesJoined(0);
                    playerToAdd.setNumberOfWrongPasswordsEntered(0);
                    
                    // Add the player to the custom whitelist
                    PlayerStatusHandler.insertNewPlayerIntoFile(playerToAdd);
                    
                    commandSender.sendMessage(Component.text()
                            .append(Component.text("[CustomWhitelistV2] Added the player ", NamedTextColor.GREEN))
                            .append(Component.text(playerName, NamedTextColor.WHITE))
                            .append(Component.text(" to the custom whitelist with the status ", NamedTextColor.GREEN))
                            .append(Component.text(newPlayerStatus.toString(), NamedTextColor.WHITE)));
                    
                } catch (Exception e) {
                    commandSender.sendMessage("[CustomWhitelistV2] Error connecting to the Mojang API");
                    return true;
                }
                
                break;
                
                
                
            case "removePlayer":
                // Remove logic here
                // Get the player name from the args
                playerName = args[1];
                
                // Get the player UUID from the player name
                playerUuid = PlayerStatusHandler.getPlayerUuidFromName(playerName);
                
                // If the playerUuid is null, give the player a message that the player is not found
                if (playerUuid == null) {
                    commandSender.sendMessage(Component.text()
                            .append(Component.text("[CustomWhitelistV2] The player ", NamedTextColor.RED))
                            .append(Component.text(playerName, NamedTextColor.WHITE))
                            .append(Component.text(" is not found", NamedTextColor.RED)));
                    return true;
                }

                PlayerStatusHandler.updatePlayerStatus(playerUuid, CWV2Player.Status.NOT_WHITELISTED);
                
                commandSender.sendMessage(Component.text()
                        .append(Component.text("[CustomWhitelistV2] Removed the player ", NamedTextColor.GREEN))
                        .append(Component.text(playerName, NamedTextColor.WHITE))
                        .append(Component.text(" from the custom whitelist", NamedTextColor.GREEN)));
                
                // Check if the removed player is currently online
                Player player = Bukkit.getPlayer(playerName);
                if (player != null) {
                    // Edit the status of the player to removed from the custom whitelist
                    PlayerStatusHandler.updatePlayerStatus(playerUuid, CWV2Player.Status.REMOVED);
                    
                    // Give the user all the effects a non whitelisted player would have
                    WhitelistHandler.disablePlayerMovementAndSight(player);
                    
                    // Send the player a message that they have been removed from the whitelist and that they can no longer join the server
                    Component kickMessage = Component.text()
                            .append(Component.text("[CustomWhitelistV2] You have been removed from the custom whitelist by a moderator. ", NamedTextColor.RED, TextDecoration.BOLD))
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
                // Get a list of all players
                List<CWV2Player> players = PlayerStatusHandler.getAllPlayers();

                // Create a new message
                
                ComponentBuilder<TextComponent, TextComponent.Builder> messageToPlayer = Component.text()
                        .append(Component.text("\n[CustomWhitelistV2] The following players are in the custom whitelist:\n", NamedTextColor.YELLOW))
                        .append(Component.text("Whitelisted players:\n", NamedTextColor.GREEN));
                
                for (CWV2Player whitelistedPlayer : players) {
                    if (whitelistedPlayer.getStatus() == CWV2Player.Status.WHITELISTED) {
                        messageToPlayer.append(Component.text(whitelistedPlayer.getUsername() + "\n", NamedTextColor.WHITE));
                    }
                }
                messageToPlayer.append(Component.text("\n\nNon-whitelisted players:\n", NamedTextColor.YELLOW));
                for (CWV2Player nonWhitelistedPlayer : players) {
                    if (nonWhitelistedPlayer.getStatus() == CWV2Player.Status.NOT_WHITELISTED) {
                        messageToPlayer.append(Component.text(nonWhitelistedPlayer.getUsername() + "\n", NamedTextColor.WHITE));
                    }
                }
                messageToPlayer.append(Component.text("\n\nOther players:", NamedTextColor.RED));
                for (CWV2Player otherPlayer : players) {
                    if (otherPlayer.getStatus() != CWV2Player.Status.WHITELISTED && otherPlayer.getStatus() != CWV2Player.Status.NOT_WHITELISTED) {
                        messageToPlayer.append(Component.text("\n" + otherPlayer.getUsername(), NamedTextColor.WHITE));
                        messageToPlayer.append(Component.text(" - ", NamedTextColor.WHITE));
                        messageToPlayer.append(Component.text(otherPlayer.getStatus().toString(), NamedTextColor.RED));
                    }
                }
                
                // Send the message to the command sender
                commandSender.sendMessage(messageToPlayer.build());
                
                break;

                
                
            case "statusOfPlayer":
                // Status logic here
                // Get the player name from the args
                playerName = args[1];
                
                // Get the player UUID from the player name
                playerUuid = PlayerStatusHandler.getPlayerUuidFromName(playerName);
                
                // If the playerUuid is null, give the player a message that the player has not joined the server yet
                if (playerUuid == null) {
                    commandSender.sendMessage(Component.text()
                            .append(Component.text("[CustomWhitelistV2] The player ", NamedTextColor.RED))
                            .append(Component.text(playerName, NamedTextColor.WHITE))
                            .append(Component.text(" has not joined the server yet", NamedTextColor.RED)));
                    return true;
                }
                
                // Get the player by its UUID
                CWV2Player playerToCheckStatus = PlayerStatusHandler.getPlayerByUUID(playerUuid);
                
                // Send the command sender a message with the status of the player
                assert playerToCheckStatus != null;
                commandSender.sendMessage(Component.text()
                        .append(Component.text("[CustomWhitelistV2] The status of the player ", NamedTextColor.GREEN))
                        .append(Component.text(playerName, NamedTextColor.WHITE))
                        .append(Component.text(" is ", NamedTextColor.GREEN))
                        .append(Component.text(playerToCheckStatus.getStatus().toString(), NamedTextColor.WHITE)));
                
                break;

                
                
            case "updatePlayerStatus":
                // Update player logic here
                // Get the player name from the args
                String playerNameToUpdate = args[1];
                
                // Get the player UUID from the player name
                String playerUuidToUpdate = PlayerStatusHandler.getPlayerUuidFromName(playerNameToUpdate);
                
                // If the playerUuid is null, give the player a message that the player is not found
                if (playerUuidToUpdate == null) {
                    commandSender.sendMessage(Component.text()
                            .append(Component.text("[CustomWhitelistV2] The player ", NamedTextColor.RED))
                            .append(Component.text(playerNameToUpdate, NamedTextColor.WHITE))
                            .append(Component.text(" is not found", NamedTextColor.RED)));
                    return true;
                }
                
                // Get the status from the args
                String status = args[2];
                
                // Check if the status is valid
                try {
                    CWV2Player.Status.valueOf(status);
                } catch (IllegalArgumentException e) {
                    commandSender.sendMessage(Component.text()
                            .append(Component.text("[CustomWhitelistV2] The status ", NamedTextColor.RED))
                            .append(Component.text(status, NamedTextColor.WHITE))
                            .append(Component.text(" is not valid", NamedTextColor.RED)));
                    return true;
                }
                
                // Check if the new status is the same as the old status
                if (Objects.requireNonNull(PlayerStatusHandler.getPlayerByUUID(playerUuidToUpdate)).getStatus() == CWV2Player.Status.valueOf(status)) {
                    commandSender.sendMessage(Component.text()
                            .append(Component.text("[CustomWhitelistV2] The status of the player ", NamedTextColor.RED))
                            .append(Component.text(playerNameToUpdate, NamedTextColor.WHITE))
                            .append(Component.text(" is already ", NamedTextColor.RED))
                            .append(Component.text(status, NamedTextColor.WHITE)));
                }
                
                // Update the status of the player
                PlayerStatusHandler.updatePlayerStatus(playerUuidToUpdate, CWV2Player.Status.valueOf(status));
                
                // Send the command sender a message that the status of the player has been updated
                commandSender.sendMessage(Component.text()
                        .append(Component.text("[CustomWhitelistV2] The status of the player ", NamedTextColor.GREEN))
                        .append(Component.text(playerNameToUpdate, NamedTextColor.WHITE))
                        .append(Component.text(" has been updated to ", NamedTextColor.GREEN))
                        .append(Component.text(status, NamedTextColor.WHITE)));
                
                // Check if the updated player is currently online
                Player playerOnline = Bukkit.getPlayer(playerNameToUpdate);
                
                if (playerOnline != null) {
                    // Send the player a message that their status has been updated
                    playerOnline.sendMessage(Component.text()
                            .append(Component.text("[CustomWhitelistV2] Your status has been updated to ", NamedTextColor.GREEN))
                            .append(Component.text(status, NamedTextColor.WHITE)));
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
                    case TEMP_BANNED, TEMP_KICKED:
                        // Get the duration of the ban from the args
                        String duration = args[3];

                        // Get the reason for the ban from the args. The reason is the rest of the args
                        StringBuilder reason = new StringBuilder();
                        for (int i = 4; i < args.length; i++) {
                            reason.append(args[i]).append(" ");
                        }

                        PlayerStatusHandler.setPlayerIsTempBannedOrTempKicked(PlayerStatusHandler.getPlayerByUUID(playerUuidToUpdate), CWV2Player.Status.valueOf(status), reason.toString(), duration);
                        
                        // If the player is online, give the player all the effects a banned player would have
                        if (playerOnline != null) {
                            WhitelistHandler.disablePlayerMovementAndSight(playerOnline);
                            
                            // Send the player a message that they have been banned or kicked and that they can no longer join the server
                            Component kickMessage = PlayerStatusHandler.getTempBanOrTempKickMessage(Objects.requireNonNull(PlayerStatusHandler.getPlayerByUUID(playerUuidToUpdate)));
                            
                            // Kick the player from the server
                            playerOnline.kick(kickMessage);
                        }
                        break;
                    case REMOVED:
                        // If the player is online, give the player all the effects a removed player would have
                        if (playerOnline != null) {
                            WhitelistHandler.disablePlayerMovementAndSight(playerOnline);
                        }
                        break;
                    default:
                        // This should never happen. Log an error message
                        PaperPluginLogger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe("The status of the player is unknown. This should never happen");
                        break;
                }
                
                break;

                
                
            case "updatePassword":
                // Update password logic here
                String newPassword = args[1];
                boolean tryUpdatePassword = PasswordHandler.updatePassword(newPassword);
                if (tryUpdatePassword) {
                    commandSender.sendMessage(Component.text()
                            .append(Component.text("[CustomWhitelistV2] Updated the password", NamedTextColor.GREEN)));
                } else {
                    commandSender.sendMessage(Component.text()
                            .append(Component.text("[CustomWhitelistV2] Error updating the password", NamedTextColor.RED)));
                }
                break;

                
                
            case "checkPassword":
                // Help logic here
                String inputPassword = args[1];
                boolean tryCheckPassword = PasswordHandler.checkPassword(inputPassword);
                if (tryCheckPassword) {
                    commandSender.sendMessage(Component.text()
                            .append(Component.text("[CustomWhitelistV2] Password is correct", NamedTextColor.GREEN)));
                } else {
                    commandSender.sendMessage(Component.text()
                            .append(Component.text("[CustomWhitelistV2] Password is incorrect", NamedTextColor.RED)));
                }
                break;



            case "help":
                // Help logic here
                String subCommand = null;
                
                try {
                    subCommand = args[1];
                } catch (ArrayIndexOutOfBoundsException ignored) {
                    
                }

                ComponentBuilder<TextComponent, TextComponent.Builder> helpMessage = null;

                switch (subCommand) {
                    case "enableOrDisableASubCommand" -> helpMessage = Component.text()
                            .append(Component.text("\n[CustomWhitelistV2] Enable or disable a subcommand\n", NamedTextColor.AQUA))
                            .append(Component.text("Usage: /customWhitelistV2 enableOrDisableASubCommand <subcommand> <enable/disable>\n", NamedTextColor.GREEN))
                            .append(Component.text("This command allows you to enable or disable a specific subcommand.", NamedTextColor.YELLOW));
                    case "listAllActivatedSubCommands" -> helpMessage = Component.text()
                            .append(Component.text("\n[CustomWhitelistV2] List all activated subcommands\n", NamedTextColor.AQUA))
                            .append(Component.text("Usage: /customWhitelistV2 listAllActivatedSubCommands\n", NamedTextColor.GREEN))
                            .append(Component.text("This command lists all the currently active subcommands.", NamedTextColor.YELLOW));
                    case "addPlayer" -> helpMessage = Component.text()
                            .append(Component.text("\n[CustomWhitelistV2] Add a player to the custom whitelist\n", NamedTextColor.AQUA))
                            .append(Component.text("Usage: /customWhitelistV2 addPlayer <playerName>\n", NamedTextColor.GREEN))
                            .append(Component.text("This command adds a specified player to the custom whitelist.", NamedTextColor.YELLOW));
                    case "removePlayer" -> helpMessage = Component.text()
                            .append(Component.text("\n[CustomWhitelistV2] Remove a player from the custom whitelist\n", NamedTextColor.AQUA))
                            .append(Component.text("Usage: /customWhitelistV2 removePlayer <playerName>\n", NamedTextColor.GREEN))
                            .append(Component.text("This command removes a specified player from the custom whitelist.", NamedTextColor.YELLOW));
                    case "listPlayers" -> helpMessage = Component.text()
                            .append(Component.text("\n[CustomWhitelistV2] List all players in the custom whitelist\n", NamedTextColor.AQUA))
                            .append(Component.text("Usage: /customWhitelistV2 listPlayers\n", NamedTextColor.GREEN))
                            .append(Component.text("This command lists all the players in the custom whitelist.", NamedTextColor.YELLOW));
                    case "statusOfPlayer" -> helpMessage = Component.text()
                            .append(Component.text("\n[CustomWhitelistV2] Get the status of a player\n", NamedTextColor.AQUA))
                            .append(Component.text("Usage: /customWhitelistV2 statusOfPlayer <playerName>\n", NamedTextColor.GREEN))
                            .append(Component.text("This command retrieves the status of a specified player.", NamedTextColor.YELLOW));
                    case "updatePlayerStatus" -> helpMessage = Component.text()
                            .append(Component.text("\n[CustomWhitelistV2] Update the status of a player\n", NamedTextColor.AQUA))
                            .append(Component.text("Usage: /customWhitelistV2 updatePlayerStatus <playerName> <status>\n", NamedTextColor.GREEN))
                            .append(Component.text("This command updates the status of a specified player.", NamedTextColor.YELLOW));
                    case "updatePassword" -> helpMessage = Component.text()
                            .append(Component.text("\n[CustomWhitelistV2] Update the password\n", NamedTextColor.AQUA))
                            .append(Component.text("Usage: /customWhitelistV2 updatePassword <newPassword>\n", NamedTextColor.GREEN))
                            .append(Component.text("This command updates the password.", NamedTextColor.YELLOW));
                    case "checkPassword" -> helpMessage = Component.text()
                            .append(Component.text("\n[CustomWhitelistV2] Check if a password is correct\n", NamedTextColor.AQUA))
                            .append(Component.text("Usage: /customWhitelistV2 checkPassword <password>\n", NamedTextColor.GREEN))
                            .append(Component.text("This command checks if the provided password is correct.", NamedTextColor.YELLOW));
                    case "help" -> helpMessage = Component.text()
                            .append(Component.text("\n[CustomWhitelistV2] Get help with the plugin\n", NamedTextColor.AQUA))
                            .append(Component.text("Usage: /customWhitelistV2 help <subcommand>\n", NamedTextColor.GREEN))
                            .append(Component.text("This command provides help for a specific subcommand.", NamedTextColor.YELLOW));
                }

                if (helpMessage == null) {
                    commandSender.sendMessage(Component.text()
                            .append(Component.text("\n[CustomWhitelistV2] Unknown subcommand ", NamedTextColor.RED))
                            .append(Component.text(subCommand, NamedTextColor.YELLOW)));
                    return true;
                }
                
                commandSender.sendMessage(helpMessage);
                break;
                
                
            default:
                commandSender.sendMessage(Component.text()
                        .append(Component.text("[CustomWhitelistV2] Unknown subcommand ", NamedTextColor.RED))
                        .append(Component.text(subcommand, NamedTextColor.WHITE)));
                break;
        }

        return true;
    }
}
