package org.sh0tix.customwhitelistv2.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.sh0tix.customwhitelistv2.handlers.PlayerStatusHandler;
import org.sh0tix.customwhitelistv2.handlers.SendMessageToPlayer;
import org.sh0tix.customwhitelistv2.handlers.WhitelistHandler;
import org.sh0tix.customwhitelistv2.whitelist.CWV2Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class EventListener implements Listener {
    
    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent playerMoveEvent) {
        // When a player moves, check if they are whitelisted. If they are not, cancel the event
        CWV2Player player = PlayerStatusHandler.FindPlayerByUUID(playerMoveEvent.getPlayer().getUniqueId().toString());
        
        if (player == null) {
            return;
        }
        
        if (player.getStatus() == CWV2Player.Status.NOT_WHITELISTED) {
            playerMoveEvent.setCancelled(true);
        }
    }
    
    
    @EventHandler
    public void event(PlayerJoinEvent playerJoinEvent) {
        // Suppress the join message
        playerJoinEvent.joinMessage(null);
        
        // When a player joins the server, check if they exist in the JSON file
        // If they do, check their status and act accordingly
        // If they don't, add them to the JSON file with the status "notWhitelisted"
        CWV2Player joinedPlayer = PlayerStatusHandler.FindPlayerByUUID(playerJoinEvent.getPlayer().getUniqueId().toString());
        
        if (joinedPlayer == null) {
            // Create a new player and add them to the JSON file
            CWV2Player newPlayer = new CWV2Player(playerJoinEvent.getPlayer().getUniqueId().toString(), playerJoinEvent.getPlayer().getName(), 1);
            PlayerStatusHandler.insertNewPlayer(newPlayer);
            
            // Player should now be in the JSON file. Retrieve the player from the JSON file and update the joinedPlayer variable
            joinedPlayer = PlayerStatusHandler.FindPlayerByUUID(playerJoinEvent.getPlayer().getUniqueId().toString());
        }
        
        // Player exists in the JSON file
        // Update the number of times the player has joined the server
        assert joinedPlayer != null;
        
        
        // Check if the user has a status of TEMP_BANNED or TEMP_KICKED and if the time has passed
        if (joinedPlayer.getStatus() == CWV2Player.Status.TEMP_BANNED) {
            // Check if the time has passed
            if (PlayerStatusHandler.getTempBanOrTempKickExpiryDate(joinedPlayer).before(new Date())) {
                // Time has passed. Set the status to NOT_WHITELISTED
                joinedPlayer.setStatus(CWV2Player.Status.NOT_WHITELISTED);
                PlayerStatusHandler.updatePlayerStatus(joinedPlayer.getUuid(), CWV2Player.Status.NOT_WHITELISTED);
            }
        } else if (joinedPlayer.getStatus() == CWV2Player.Status.TEMP_KICKED) {
            if (PlayerStatusHandler.getTempBanOrTempKickExpiryDate(joinedPlayer).before(new Date())) {
                // Time has passed. Set the status to WHITELISTED
                joinedPlayer.setStatus(CWV2Player.Status.WHITELISTED);
                PlayerStatusHandler.updatePlayerStatus(joinedPlayer.getUuid(), CWV2Player.Status.WHITELISTED);
            }
        }
        
        
        PlayerStatusHandler.updateNumberOfTimesJoined(joinedPlayer.getUuid());

        // Check their status and act accordingly
        switch (joinedPlayer.getStatus()) {
            case WHITELISTED -> {
                // Player is whitelisted. Allow them to join the server
                // Make sure the user is able to see, move and chat
                WhitelistHandler.enablePlayerMovementAndSight(playerJoinEvent.getPlayer());
                
                /* Construct the message that will be send to the player
                * The message will look like this:
                *   "Welcome back to the server <player name>"
                *   "You have joined the server <number of times joined> times"
                 */
                SendMessageToPlayer message = new SendMessageToPlayer();
                message.addPartToMessage("Welcome back to the server " + playerJoinEvent.getPlayer().getName(), Color.YELLOW);
                
                // Tell the user they are welcome back to the server
                playerJoinEvent.getPlayer().sendMessage(message.getMessage());
                
                // Make sure numberOfTimesJoined is not null, minimum value is 1
                int numberOfTimesJoined = joinedPlayer.getNumberOfTimesJoined();
                Component joinedMessage;
                if (numberOfTimesJoined < 1) {
                    joinedPlayer.setNumberOfTimesJoined(1);
                    joinedMessage = Component.text(
                            "§a" + playerJoinEvent.getPlayer().getName() + " just joined the server. He has joined the server " +
                                    joinedPlayer.getNumberOfTimesJoined() + " time"
                    );
                } else {
                    // Broadcast to the server that the player has joined. Also tell everyone how many times the player has joined the server
                    joinedMessage = Component.text(
                            "§a" + playerJoinEvent.getPlayer().getName() + " just joined the server. He has joined the server " +
                                    numberOfTimesJoined + " times"
                    );
                }
                
                Bukkit.getServer().broadcast(joinedMessage);
            }
            case NOT_WHITELISTED -> {
                // Player is not whitelisted. Disable movement, the ability to see, chat and tell the user to insert the correct password
                WhitelistHandler.disablePlayerMovementAndSight(playerJoinEvent.getPlayer());

                // Make sure the user has the role to execute the login command. If they don't, add it to them
                LuckPerms luckPerms = LuckPermsProvider.get();
                User user = luckPerms.getUserManager().getUser(playerJoinEvent.getPlayer().getUniqueId());
                if (user != null) {
                    if (!playerJoinEvent.getPlayer().hasPermission("customwhitelistv2.login")) {
                        PermissionNode node = PermissionNode.builder("customwhitelistv2.login").value(true).build();
                        user.data().add(node);
                        luckPerms.getUserManager().saveUser(user);
                    }
                }

                /*
                * Construct the message that will be send to the player
                * The message will look like this:
                *   "Welcome to the server <player name>"
                *   "You are currently not whitelisted on the server. In order to join, please insert the correct password"
                *   "You can insert the password by typing /login <password>"
                *   "If you don't have a password, please contact an admin"
                 */
                SendMessageToPlayer message = new SendMessageToPlayer();
                message.addPartToMessage("Welcome to the server " + playerJoinEvent.getPlayer().getName() + "\n", Color.YELLOW);
                message.addPartToMessage("You are currently not whitelisted on the server. In order to join, please insert the correct password\n", Color.YELLOW);
                message.addPartToMessage("You can insert the password by typing ", Color.YELLOW);
                message.addPartToMessage("/login <password>\n", Color.GREEN);
                message.addPartToMessage("If you don't have a password, please contact an admin", Color.YELLOW);
                
                // Player is not whitelisted. Tell the user to insert the correct password.
                playerJoinEvent.getPlayer().sendMessage(message.getMessage());
            }
            case BANNED -> {
                // Player is banned. This should be handled by the server and the ban list.
            }
            case KICKED -> {
                // Player status is kicked.
            }
            case TEMP_BANNED -> {
                // Player is temporarily banned. Kick them from the server and tell them when they can join again
                // Difference between TEMP_BANNED and TEMP_KICKED is that TEMP_BANNED is a ban and the player is not whitelisted, after the time has passed and has to input the password again

                // Make sure the user is not able to see and move
                WhitelistHandler.disablePlayerMovementAndSight(playerJoinEvent.getPlayer());
                
                // Get the date when the ban expires
                Date banExpiryDate = PlayerStatusHandler.getTempBanOrTempKickExpiryDate(joinedPlayer);

                // Create a SimpleDateFormat object with the desired format
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy - HH:mm");

                // Format the ban expiry date into a string
                String formattedBanExpiryDate = sdf.format(banExpiryDate);
                
                // Generate kick message
                Component kickMessage = Component.text()
                        .append(Component.text("You have been temporarily banned from the server.\n", NamedTextColor.RED, TextDecoration.BOLD))
                        .append(Component.text("Reason: ", NamedTextColor.RED, TextDecoration.BOLD))
                        .append(Component.text(PlayerStatusHandler.getTempBanOrTempKickMessage(joinedPlayer) + "\n", NamedTextColor.WHITE))
                        .append(Component.text("You can join the server again on ", NamedTextColor.RED, TextDecoration.BOLD))
                        .append(Component.text(formattedBanExpiryDate, NamedTextColor.WHITE, TextDecoration.BOLD))
                        .append(Component.text("\nIf you think this is a mistake, please contact a moderator or administrator", NamedTextColor.RED, TextDecoration.BOLD))
                        .build();
                
                // Kick the player again with the kick message
                playerJoinEvent.getPlayer().kick(kickMessage);
            }
            case TEMP_KICKED -> {
                // Player is temporarily kicked. Kick them from the server and tell them when they can join again. 
                // Difference between TEMP_BANNED and TEMP_KICKED is that TEMP_KICKED is not a ban, but a kick and the player is still whitelisted, after the time has passed

                // Make sure the user is not able to see and move
                WhitelistHandler.disablePlayerMovementAndSight(playerJoinEvent.getPlayer());

                // Kick the player again with the kick message
                playerJoinEvent.getPlayer().kick(PlayerStatusHandler.getTempBanOrTempKickMessage(joinedPlayer));
            }
            
            case REMOVED -> {
                // Player is removed from the custom whitelist. Disable movement, the ability to see, chat and tell the user they have been removed from the whitelist
                WhitelistHandler.disablePlayerMovementAndSight(playerJoinEvent.getPlayer());
                
                /*
                * Construct the message that will be send to the player
                * The message will look like this:
                *   "You have been removed from the custom whitelist by a moderator"
                *   "You can no longer play on the server"
                *   "If you think this is a mistake, please contact a moderator or administrator"
                 */
                Component kickMessage = Component.text()
                        .append(Component.text("You have been removed from the custom whitelist by a moderator. ", NamedTextColor.RED, TextDecoration.BOLD))
                        .append(Component.text("You can no longer play on the server. ", NamedTextColor.RED, TextDecoration.BOLD))
                        .append(Component.text("If you think this is a mistake, ", NamedTextColor.RED, TextDecoration.BOLD))
                        .append(Component.text("click here", NamedTextColor.BLUE, TextDecoration.UNDERLINED)
                                .hoverEvent(HoverEvent.showText(Component.text("Click to send a help requests to the moderators", NamedTextColor.GREEN)))
                                .clickEvent(ClickEvent.runCommand("/mm " + Objects.requireNonNull(playerJoinEvent.getPlayer().getName()) + " needs help with the custom whitelist. Please help them.")))
                        .append(Component.text(" or contact an administrator.", NamedTextColor.RED, TextDecoration.BOLD))
                        .append(Component.text("\nIf you want to write a custom message to the moderators, ", NamedTextColor.RED, TextDecoration.BOLD))
                        .append(Component.text("you can use the command ", NamedTextColor.RED, TextDecoration.BOLD))
                        .append(Component.text("/msgmoderator <message>", NamedTextColor.BLUE, TextDecoration.UNDERLINED))
                        .build();
                
                // Player is removed from the custom whitelist. Tell the user they have been removed from the whitelist
                playerJoinEvent.getPlayer().sendMessage(kickMessage);
            }
        }
    }
}
