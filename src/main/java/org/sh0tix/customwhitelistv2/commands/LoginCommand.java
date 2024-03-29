package org.sh0tix.customwhitelistv2.commands;

import net.kyori.adventure.text.Component;
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
import org.sh0tix.customwhitelistv2.handlers.PasswordHandler;
import org.sh0tix.customwhitelistv2.handlers.PlayerStatusHandler;
import org.sh0tix.customwhitelistv2.handlers.WhitelistHandler;
import org.sh0tix.customwhitelistv2.whitelist.CWV2Player;

public class LoginCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String string, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("[CustomWhitelistV2] This command can only be executed by a player");
            return true;
        }
        
        Player player = (Player) commandSender;
        CWV2Player cwv2Player = PlayerStatusHandler.getPlayerByUUID(String.valueOf(player.getUniqueId()));
        
        if (args.length == 0) {
            commandSender.sendMessage("[CustomWhitelistV2] Please provide a password");
            return true;
        }
        
        String password = args[0];

        if (PasswordHandler.checkPassword(password)) {
            // Password is correct. The user is allowed to play on the server
            commandSender.sendMessage("[CustomWhitelistV2] Password is correct");
            
            // Update the status of the player in the JSON file
            assert cwv2Player != null;
            PlayerStatusHandler.updatePlayerStatus(cwv2Player.getUuid(), CWV2Player.Status.WHITELISTED);
            
            // Remove the permission node for the login command from the player
            LuckPerms luckPerms = LuckPermsProvider.get();
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user != null) {
                PermissionNode node = PermissionNode.builder("customwhitelistv2.login").value(false).build();
                user.data().add(node);
                luckPerms.getUserManager().saveUser(user);
            }
            // Broadcast a message to all players that the player has joined the server and is now whitelisted
            Component message = Component.text("§a[CustomWhitelistV2] " + player.getName() + " has joined the server and is now whitelisted!");
            
            Bukkit.getServer().broadcast(message);
            
            // Reset the number of joined times and wrong passwords entered to 0
            PlayerStatusHandler.updateNumberOfTimesJoined(cwv2Player.getUuid(), 0);
            PlayerStatusHandler.updateNumberOfWrongPasswordsEntered(cwv2Player.getUuid(), 0);
            
            // Remove the blindness effect and allow the player to move
            WhitelistHandler.enablePlayerMovementAndSight(player);
        } else {
            // Password in incorrect.
            commandSender.sendMessage("[CustomWhitelistV2] Password is incorrect");
            
            // Update the number of wrong passwords entered
            assert cwv2Player != null;
            PlayerStatusHandler.updateNumberOfWrongPasswordsEntered(cwv2Player.getUuid());
        }
        
        return true;
    }
}
