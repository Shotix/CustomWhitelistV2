package org.sh0tix.customwhitelistv2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.sh0tix.customwhitelistv2.commands.CustomWhitelistV2Commands;
import org.sh0tix.customwhitelistv2.commands.AdminCommand;
import org.sh0tix.customwhitelistv2.commands.LoginCommand;
import org.sh0tix.customwhitelistv2.commands.MsgModeratorCommand;
import org.sh0tix.customwhitelistv2.handlers.LocalizationHandler;
import org.sh0tix.customwhitelistv2.handlers.LuckPermsHandler;
import org.sh0tix.customwhitelistv2.listener.ChatListener;
import org.sh0tix.customwhitelistv2.listener.EventListener;

import java.io.*;
import java.util.Objects;

import static org.bukkit.Bukkit.getServer;

public final class CustomWhitelistV2 extends JavaPlugin {
    
    private FileConfiguration localization;

    private static CustomWhitelistV2 instance;

    private boolean debugMode = true;
    
    private LocalizationHandler localizationHandler;
    
    public static LocalizationHandler getLocalizationHandler() {
        return instance.localizationHandler;
    }
    
    public static boolean getDebugMode() {
        return instance.debugMode;
    }
    
    public static void setDebugMode(boolean debugMode) {
        instance.debugMode = debugMode;
        
    }
    
    public static CustomWhitelistV2 getInstance() {
        return instance;
    }
    
    @Override
    public void onEnable() {
        localizationHandler = new LocalizationHandler(this);
        
        // Set up localisation and load the selected language
        localizationHandler.saveDefaultLocalizationFile("en_US.yml");
        localizationHandler.saveDefaultLocalizationFile("de_DE.yml");
        localizationHandler.loadLocalization(localizationHandler.loadSelectedLanguage());
        getLogger().info(localizationHandler.getLocalisedString("CustomWhitelistV2.onEnable.localization_initialization"));
        
        
        // Check if PaperMC is used 
        if (!getServer().getVersion().contains("Paper")) {
            getLogger().severe(localizationHandler.getLocalisedString("CustomWhitelistV2.onEnable.papermc_not_found"));
            return;
        }

        // Check if the LuckPerms plugin is installed
        if (getServer().getPluginManager().getPlugin("LuckPerms") == null) {
            getLogger().severe(localizationHandler.getLocalisedString("CustomWhitelistV2.onEnable.luckperms_not_found.message_not_found"));
            getLogger().severe(localizationHandler.getLocalisedString("CustomWhitelistV2.onEnable.luckperms_not_found.message_installation_information"));
            getLogger().severe(localizationHandler.getLocalisedString("CustomWhitelistV2.onEnable.luckperms_not_found.message_install_link"));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Check if the LuckPerms plugin is installed and enabled
        if (!LuckPermsHandler.setupLuckPerms()) {
            getLogger().severe(localizationHandler.getLocalisedString("CustomWhitelistV2.onEnable.luckperms_initialization"));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Send a message to the console
        getLogger().info(localizationHandler.getLocalisedString("CustomWhitelistV2.onEnable.plugin_enabled_successfully.console_message"));

        // Send a message to all administrators
        for (Player player : getServer().getOnlinePlayers()) {
            if (player.hasPermission("customwhitelistv2.administrator")) {
                player.sendMessage(Component.text().append(Component.text(localizationHandler.getLocalisedString("CustomWhitelistV2.onDisable.plugin_enabled_successfully.administrator_message"), NamedTextColor.RED, TextDecoration.BOLD)));
            }
        }

        // Plugin startup logic
        instance = this;
        
        // Register the events
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        
        // Register the commands
        Objects.requireNonNull(getCommand("customWhitelistV2Admin")).setExecutor(new AdminCommand());
        Objects.requireNonNull(getCommand("customWhitelistV2Admin")).setTabCompleter(new CustomWhitelistV2AdminTabCompleter());
        Objects.requireNonNull(getCommand("msgModerator")).setExecutor(new MsgModeratorCommand());
        Objects.requireNonNull(getCommand("login")).setExecutor(new LoginCommand());
        Objects.requireNonNull(getCommand("customWhitelistV2")).setTabCompleter(new CustomWhitelistV2TabCompleter());
        Objects.requireNonNull(getCommand("customWhitelistV2")).setExecutor(new CustomWhitelistV2Commands());
    }
    

    @Override
    public void onDisable() {
        // Cancel all tasks associated with this plugin
        getServer().getScheduler().cancelTasks(this);

        // Send a message to the console
        getServer().getConsoleSender().sendMessage(localizationHandler.getLocalisedString("CustomWhitelistV2.onDisable.plugin_disabled_successfully.console_message"));

        // Send a message to all administrators
        for (Player player : getServer().getOnlinePlayers()) {
            if (player.hasPermission("customwhitelistv2.administrator")) {
                player.sendMessage(Component.text().append(Component.text(localizationHandler.getLocalisedString("CustomWhitelistV2.onDisable.plugin_disabled_successfully.administrator_message"), NamedTextColor.RED, TextDecoration.BOLD)));
            }
        }

        // Nullify any static instances to help with garbage collection
        instance = null;
    }


    
}
