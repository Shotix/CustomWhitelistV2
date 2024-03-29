package org.sh0tix.customwhitelistv2.handlers;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.sh0tix.customwhitelistv2.whitelist.CWV2Player;

import java.util.HashSet;
import java.util.List;

public class WhitelistHandler {
    
    private final HashSet<CWV2Player> nonWhitelistedPlayers = new HashSet<>();
    private final HashSet<CWV2Player> whitelistedPlayers = new HashSet<>();
    
    public WhitelistHandler() {
        retrieveAllNonWhitelistedPlayersFromJSON();
    }
    
    private void retrieveAllNonWhitelistedPlayersFromJSON() {
        List<CWV2Player> playersList = PlayerStatusHandler.getAllNonWhitelistedPlayers();

        nonWhitelistedPlayers.addAll(playersList);
    }
    
    public boolean isPlayerNotWhitelisted(String playerUuid) {
        for (CWV2Player player : nonWhitelistedPlayers) {
            if (player.getUuid().equals(playerUuid)) {
                return true;
            }
        }
        return false;
    }
    
    public static void disablePlayerMovementAndSight(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, Integer.MAX_VALUE, false, false));
        player.setGameMode(org.bukkit.GameMode.SPECTATOR);
        player.setJumping(false);
        player.setInvisible(true);
        player.setWalkSpeed(0);
        player.setFlySpeed(0);
        player.setFlying(false);
    }
    
    public static void enablePlayerMovementAndSight(Player player) {
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.setGameMode(org.bukkit.GameMode.SURVIVAL);
        player.setInvisible(false);
        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.1f);
        player.setFlying(false);
    }
}
