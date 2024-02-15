package org.sh0tix.customwhitelistv2.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.sh0tix.customwhitelistv2.handlers.WhitelistHandler;

import java.util.Objects;


public class ChatListener implements Listener {
    
    @EventHandler
    public void onChat(AsyncChatEvent event) {
        WhitelistHandler whitelistHandler = new WhitelistHandler();

        if (whitelistHandler.isPlayerNotWhitelisted(event.getPlayer().getUniqueId().toString())) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTask(Objects.requireNonNull(
                    Bukkit.getPluginManager().getPlugin("CustomWhitelistV2")), () -> 
                    event.getPlayer().sendMessage("You are not whitelisted! Please login using /login <password> to chat!")
            );
        }
    }
}