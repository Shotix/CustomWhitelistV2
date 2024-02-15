package org.sh0tix.customwhitelistv2;

import org.bukkit.plugin.java.JavaPlugin;
import org.sh0tix.customwhitelistv2.commands.CustomWhitelistV2Commands;
import org.sh0tix.customwhitelistv2.commands.LoginCommand;
import org.sh0tix.customwhitelistv2.listener.ChatListener;
import org.sh0tix.customwhitelistv2.listener.EventListener;

import java.util.Objects;

public final class CustomWhitelistV2 extends JavaPlugin {
    
    private static CustomWhitelistV2 instance;
    
    public static CustomWhitelistV2 getInstance() {
        return instance;
    }
    
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        // Register the events
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        
        // Register the commands
        Objects.requireNonNull(getCommand("login")).setExecutor(new LoginCommand());
        Objects.requireNonNull(getCommand("customWhitelistV2")).setTabCompleter(new CustomWhitelistV2TabCompleter());
        Objects.requireNonNull(getCommand("customWhitelistV2")).setExecutor(new CustomWhitelistV2Commands());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
