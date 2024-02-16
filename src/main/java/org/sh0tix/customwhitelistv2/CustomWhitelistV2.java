package org.sh0tix.customwhitelistv2;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.plugin.java.JavaPlugin;
import org.sh0tix.customwhitelistv2.commands.CustomWhitelistV2Commands;
import org.sh0tix.customwhitelistv2.commands.DebugCommand;
import org.sh0tix.customwhitelistv2.commands.LoginCommand;
import org.sh0tix.customwhitelistv2.commands.MsgModeratorCommand;
import org.sh0tix.customwhitelistv2.listener.ChatListener;
import org.sh0tix.customwhitelistv2.listener.EventListener;

import java.util.Objects;

public final class CustomWhitelistV2 extends JavaPlugin {
    
    public static boolean debugMode = false;
    
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
        Objects.requireNonNull(getCommand("customWhitelistV2Debugging")).setExecutor(new DebugCommand());
        Objects.requireNonNull(getCommand("customWhitelistV2Debugging")).setTabCompleter(new CustomWhitelistV2DebuggingTabCompleter());
        Objects.requireNonNull(getCommand("msgModerator")).setExecutor(new MsgModeratorCommand());
        Objects.requireNonNull(getCommand("login")).setExecutor(new LoginCommand());
        Objects.requireNonNull(getCommand("customWhitelistV2")).setTabCompleter(new CustomWhitelistV2TabCompleter());
        Objects.requireNonNull(getCommand("customWhitelistV2")).setExecutor(new CustomWhitelistV2Commands());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
