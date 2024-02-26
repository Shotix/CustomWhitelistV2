package org.sh0tix.customwhitelistv2.handlers;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.types.PermissionNode;
import org.sh0tix.customwhitelistv2.CustomWhitelistV2;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class LuckPermsHandler {
    public static boolean setupLuckPerms() {

        // Check if the necessary permissions are existing and add them if not
        LuckPerms api = LuckPermsProvider.get();
        Group group = api.getGroupManager().getGroup("default");
        if (group == null) {
            getLogger().severe("[CustomWhitelistV2] The group 'default' does not exist! Disabling CustomWhitelistV2...");
            getServer().getPluginManager().disablePlugin(CustomWhitelistV2.getInstance());
            return false;
        }

        // Create the permissions
        PermissionNode.builder("customwhitelistv2.manage").build();
        PermissionNode.builder("customwhitelistv2.administrator").build();
        PermissionNode.builder("customwhitelistv2.login").build();

        // Create the manage group and add permissions
        Group manageGroup = api.getGroupManager().getGroup("customwhitelistv2.manage");
        if (manageGroup == null) {
            manageGroup = api.getGroupManager().createAndLoadGroup("customwhitelistv2.manage").join();
            manageGroup.data().add(PermissionNode.builder("customwhitelistv2.manage").build());
            api.getGroupManager().saveGroup(manageGroup);
        }

        // Create the administrator group and add permissions
        Group administratorGroup = api.getGroupManager().getGroup("customwhitelistv2.administrator");
        if (administratorGroup == null) {
            administratorGroup = api.getGroupManager().createAndLoadGroup("customwhitelistv2.administrator").join();
            administratorGroup.data().add(PermissionNode.builder("customwhitelistv2.administrator").build());
            api.getGroupManager().saveGroup(administratorGroup);
        }

        // Create the login group and add permissions
        Group loginGroup = api.getGroupManager().getGroup("customwhitelistv2.login");
        if (loginGroup == null) {
            loginGroup = api.getGroupManager().createAndLoadGroup("customwhitelistv2.login").join();
            loginGroup.data().add(PermissionNode.builder("customwhitelistv2.login").build());
            api.getGroupManager().saveGroup(loginGroup);
        }

        return true;
    }
}
