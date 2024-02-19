package org.sh0tix.customwhitelistv2;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.sh0tix.customwhitelistv2.commands.CustomWhitelistV2Commands;
import org.sh0tix.customwhitelistv2.commands.DebugCommand;
import org.sh0tix.customwhitelistv2.commands.LoginCommand;
import org.sh0tix.customwhitelistv2.commands.MsgModeratorCommand;
import org.sh0tix.customwhitelistv2.listener.ChatListener;
import org.sh0tix.customwhitelistv2.listener.EventListener;

import java.util.Objects;

public final class CustomWhitelistV2 extends JavaPlugin {
    
    public static boolean debugMode = true;
    
    private static CustomWhitelistV2 instance;
    
    public static CustomWhitelistV2 getInstance() {
        return instance;
    }
    
    @Override
    public void onEnable() {
        // Check if PaperMC is used 
        if (!getServer().getVersion().contains("Paper")) {
            getLogger().severe("[CustomWhitelistV2] This plugin is designed to work with PaperMC only! Disabling CustomWhitelistV2...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Check if the LuckPerms plugin is installed and enabled
        if (checkForLuckPermsSetup()) return;
        
        // Send a message to the console
        getServer().getConsoleSender().sendMessage("[CustomWhitelistV2] CustomWhitelistV2 plugin has been enabled!");

        // Send a message to all administrators
        for (Player player : getServer().getOnlinePlayers()) {
            if (player.hasPermission("customwhitelistv2.administrator")) {
                player.sendMessage(Component.text().append(Component.text("[CustomWhitelistV2] CustomWhitelistV2 plugin has been enabled!", NamedTextColor.GREEN, TextDecoration.BOLD)));
            }
        }

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

    private boolean checkForLuckPermsSetup() {
        // Check if the LuckPerms plugin is installed
        if (getServer().getPluginManager().getPlugin("LuckPerms") == null) {
            getLogger().severe("[CustomWhitelistV2] LuckPerms plugin not found! Disabling CustomWhitelistV2...");
            getServer().getPluginManager().disablePlugin(this);
            return true;
        }

        // Check if the LuckPerms plugin is enabled
        if (!Objects.requireNonNull(getServer().getPluginManager().getPlugin("LuckPerms")).isEnabled()) {
            getLogger().severe("[CustomWhitelistV2] LuckPerms plugin is not enabled! Disabling CustomWhitelistV2...");
            getServer().getPluginManager().disablePlugin(this);
            return true;
        }

        // Check if the necessary permissions are existing and add them if not
        LuckPerms api = LuckPermsProvider.get();
        Group group = api.getGroupManager().getGroup("default");
        if (group == null) {
            getLogger().severe("[CustomWhitelistV2] The group 'default' does not exist! Disabling CustomWhitelistV2...");
            getServer().getPluginManager().disablePlugin(this);
            return true;
        }

        // Create the permissions
        PermissionNode.builder("customwhitelistv2.manage").build();
        PermissionNode.builder("customwhitelistv2.administrator").build();
        PermissionNode.builder("customwhitelistv2.login").build();
        return false;
    }
    

    @Override
    public void onDisable() {
        // Cancel all tasks associated with this plugin
        getServer().getScheduler().cancelTasks(this);

        // Send a message to the console
        getServer().getConsoleSender().sendMessage("[CustomWhitelistV2] CustomWhitelistV2 plugin has been disabled!");

        // Send a message to all administrators
        for (Player player : getServer().getOnlinePlayers()) {
            if (player.hasPermission("customwhitelistv2.administrator")) {
                player.sendMessage(Component.text().append(Component.text("[CustomWhitelistV2] CustomWhitelistV2 plugin has been disabled!", NamedTextColor.RED, TextDecoration.BOLD)));
            }
        }

        // Nullify any static instances to help with garbage collection
        instance = null;
    }
}
