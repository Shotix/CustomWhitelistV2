package org.sh0tix.customwhitelistv2.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import net.luckperms.api.model.user.UserManager;
import org.sh0tix.customwhitelistv2.CustomWhitelistV2;
import org.sh0tix.customwhitelistv2.handlers.LocalizationHandler;
import org.sh0tix.customwhitelistv2.handlers.PlayerStatusHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminCommand implements CommandExecutor {
    
    private final LocalizationHandler localizationHandler;
    
    public AdminCommand() {
        this.localizationHandler = CustomWhitelistV2.getLocalizationHandler();
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String string, @NotNull String[] args) {
        if (args.length == 0) {
            return false;
        }
        
        String firstSubCommand = args[0];
        
        switch (firstSubCommand) {
            case "debug" -> {
                if (args.length < 2) {
                    commandSender.sendMessage(Component.text(localizationHandler.getLocalisedString("AdminCommand.onCommand.debug.usage"), NamedTextColor.RED));
                    return true;
                }
                
                String newState = args[1];
                if (newState.equals("true")) {
                    CustomWhitelistV2.setDebugMode(true);
                    commandSender.sendMessage(Component.text(localizationHandler.getLocalisedString("AdminCommand.onCommand.debug.enabled"), NamedTextColor.GREEN));
                } else if (newState.equals("false")) {
                    CustomWhitelistV2.setDebugMode(false);
                    commandSender.sendMessage(Component.text(localizationHandler.getLocalisedString("AdminCommand.onCommand.debug.disabled"), NamedTextColor.GREEN));
                } else {
                    commandSender.sendMessage(Component.text(localizationHandler.getLocalisedString("AdminCommand.onCommand.debug.usage"), NamedTextColor.RED));
                }
                return true;
            }
            
            case "addModerator" -> {
                if (args.length < 2) {
                    commandSender.sendMessage(Component.text(localizationHandler.getLocalisedString("AdminCommand.addModerator.usage"), NamedTextColor.RED));
                    return true;
                }
                
                addFromCustomWhitelist(commandSender, args);
                
                return true;
            }
            
            case "removeModerator" -> {
                // Remove a moderator
                if (args.length < 2) {
                    commandSender.sendMessage(Component.text(localizationHandler.getLocalisedString("AdminCommand.removeModerator.usage"), NamedTextColor.RED));
                    return true;
                }
                
                List<Player> moderators = listAllCWV2Moderators();
                
                for (Player moderator : moderators) {
                    if (moderator.getName().equals(args[1])) {
                        // Remove the moderator
                        LuckPerms api = LuckPermsProvider.get();
                        UserManager usersManager = api.getUserManager();
                        User user = usersManager.getUser(moderator.getUniqueId());
                        PermissionNode node = PermissionNode.builder("customwhitelistv2.manage").value(true).build();
                        assert user != null;
                        user.data().remove(node);
                        api.getUserManager().saveUser(user);
                        
                        // Message the moderator, that he has been removed (if they are online)
                        if (moderator.isOnline()) {
                            moderator.sendMessage(Component.text(localizationHandler.getLocalisedString("AdminCommand.removeModerator.youWereRemoved"), NamedTextColor.RED, TextDecoration.BOLD));
                        }
                        
                        // Message the command sender, that the moderator has been removed
                        commandSender.sendMessage(Component.text("\n[CustomWhitelistV2] " + moderator.getName() + localizationHandler.getLocalisedString("AdminCommand.removeModerator.aModeratorWasRemoved"), NamedTextColor.GREEN, TextDecoration.BOLD));
                    }
                }
                return true;
            }
            
            case "listAllModerators" -> {
                // List all users with the permission customwhitelistv2.manage
                // Retrieve all players with the permission customwhitelistv2.manage
                List<Player> moderators = listAllCWV2Moderators();
                
                if (moderators.isEmpty()) {
                    commandSender.sendMessage(Component.text(localizationHandler.getLocalisedString("AdminCommand.onCommand.listAllModerators.noModerators"), NamedTextColor.RED));
                    return true;
                }
                
                commandSender.sendMessage(Component.text(localizationHandler.getLocalisedString("AdminCommand.onCommand.listAllModerators.moderators"), NamedTextColor.YELLOW, TextDecoration.BOLD));
                for (Player moderator : moderators) {
                    commandSender.sendMessage(Component.text("\n" + moderator.getName(), NamedTextColor.GREEN));
                }
                return true;
            }
            
            case "setPluginLanguage" -> {
                if (args.length < 2) {
                    commandSender.sendMessage(Component.text("\nUsage: /customwhitelistv2admin setPluginLanguage <languageCode>", NamedTextColor.RED));
                    return true;
                }
                
                // Set the plugin language
                String languageCode = args[1];
                if (languageCode.equals("de_DE") || languageCode.equals("en_US")) {
                    CustomWhitelistV2.getLocalizationHandler().saveSelectedLanguage(languageCode);
                    CustomWhitelistV2.getLocalizationHandler().loadLocalization(languageCode);
                    commandSender.sendMessage(Component.text("\n[CustomWhitelistV2] Plugin language set to " + languageCode, NamedTextColor.GREEN));
                    return true;
                }
                
                commandSender.sendMessage(Component.text("\n[CustomWhitelistV2] Invalid language code", NamedTextColor.RED));
                return true;
            }
        }
        return false;
    }

    private void addFromCustomWhitelist(CommandSender commandSender, String[] args) {
        // Get the player UUID from the args
        String playerName = args[1];
        String playerUUID = PlayerStatusHandler.getPlayerUuidFromName(playerName);

        if (playerUUID == null) {
            // Write a message to the command sender, that the player hasn't joined the server yet
            commandSender.sendMessage(Component.text()
                    .append(Component.text(localizationHandler.getLocalisedString("AdminCommand.addFromCustomWhitelist.playerHasNotJoinedYet.firstPart"), NamedTextColor.RED))
                    .append(Component.text(playerName, NamedTextColor.GREEN, TextDecoration.BOLD))
                    .append(Component.text(localizationHandler.getLocalisedString("AdminCommand.addFromCustomWhitelist.playerHasNotJoinedYet.secondPart"), NamedTextColor.RED))
                    .build());
            return;
        }

        // Log the playerName and the playerUUID to the console if the debug mode is enabled
        if (CustomWhitelistV2.getDebugMode()) {
            CustomWhitelistV2.getInstance().getLogger().info(localizationHandler.getLocalisedString("AdminCommand.addFromCustomWhitelist.logging.playerName") + playerName + localizationHandler.getLocalisedString("AdminCommand.addFromCustomWhitelist.logging.playerUUID") + playerUUID);
        }

        LuckPerms luckPerms = LuckPermsProvider.get();
        UserManager userManager = luckPerms.getUserManager();

        // Check if the user is already a moderator
        User user = userManager.getUser(UUID.fromString(playerUUID));
        if (user != null) {
            if (user.getCachedData().getPermissionData().checkPermission("customwhitelistv2.manage").asBoolean()) {
                // Send the command sender a message, that the player is already a moderator
                commandSender.sendMessage(Component.text("\n[CustomWhitelistV2] " + playerName + localizationHandler.getLocalisedString("AdminCommand.addFromCustomWhitelist.playerIsAlreadyModerator"), NamedTextColor.RED));
                
                return;
            }
            user.data().add(PermissionNode.builder("customwhitelistv2.manage").value(true).build());
            
            // Check if the user now has the permission
            if (user.getCachedData().getPermissionData().checkPermission("customwhitelistv2.manage").asBoolean()) {
                // Message the player, that he has been added to the CustomWhitelistV2 moderators
                Player player = Bukkit.getPlayer(UUID.fromString(playerUUID));
                if (player != null) {
                    player.sendMessage(Component.text(localizationHandler.getLocalisedString("AdminCommand.addFromCustomWhitelist.messageToPlayer"), NamedTextColor.GREEN, TextDecoration.BOLD));
                }
                
                // Message the command sender, that the player has been added
                CustomWhitelistV2.getInstance().getServer().getConsoleSender().sendMessage(Component.text("\n[CustomWhitelistV2] " + playerName + localizationHandler.getLocalisedString("AdminCommand.addFromCustomWhitelist.playerHasBeenAdded"), NamedTextColor.GREEN, TextDecoration.BOLD));
                return;
            }
        }
    }

    private List<Player> listAllCWV2Moderators() {
        // Retrieve all players with the permission customwhitelistv2.manage
        LuckPerms api = LuckPermsProvider.get();
        UserManager usersManager = api.getUserManager();
        List<Player> moderators = new ArrayList<>();
        
        usersManager.getLoadedUsers().forEach(user -> {
            if (user.getCachedData().getPermissionData().checkPermission("customwhitelistv2.manage").asBoolean()) {
                moderators.add(Bukkit.getPlayer(user.getUniqueId()));
            }
        });
        
        return moderators;
    }
}
