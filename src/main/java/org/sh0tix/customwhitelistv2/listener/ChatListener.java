package org.sh0tix.customwhitelistv2.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.sh0tix.customwhitelistv2.CustomWhitelistV2;
import org.sh0tix.customwhitelistv2.handlers.WhitelistHandler;

import java.util.Objects;


public class ChatListener implements Listener {
    
    @EventHandler
    public void onChat(AsyncChatEvent event) {
        WhitelistHandler whitelistHandler = new WhitelistHandler();
        
        if (CustomWhitelistV2.getDebugMode()) {
            Bukkit.getLogger().info("ChatListener: " + event.getPlayer().getName() + " tried to chat\nPlayer UUID: " + event.getPlayer().getUniqueId() + "\nIs the player whitelisted?: " + !whitelistHandler.isPlayerNotWhitelisted(event.getPlayer().getUniqueId().toString()));
        }

        if (whitelistHandler.isPlayerNotWhitelisted(event.getPlayer().getUniqueId().toString())) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTask(Objects.requireNonNull(
                    Bukkit.getPluginManager().getPlugin("CustomWhitelistV2")), () -> 
                    event.getPlayer().sendMessage(Component.text()
                            .append(Component.text("[CustomWhitelistV2] ", net.kyori.adventure.text.format.NamedTextColor.GREEN))
                            .append(Component.text("You are not whitelisted on this server", net.kyori.adventure.text.format.NamedTextColor.YELLOW))
                            .append(Component.text(" and therefore not allowed to \n", net.kyori.adventure.text.format.NamedTextColor.YELLOW))
                            .append(Component.text("Please login with the command ", net.kyori.adventure.text.format.NamedTextColor.YELLOW))
                            .append(Component.text("/login <password>", net.kyori.adventure.text.format.NamedTextColor.AQUA))
                            .append(Component.text(" to play on this server", net.kyori.adventure.text.format.NamedTextColor.YELLOW))
            ));
        }
    }
}