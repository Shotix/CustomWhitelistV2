package org.sh0tix.customwhitelistv2.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.sh0tix.customwhitelistv2.ComponentTypeAdapter;
import org.sh0tix.customwhitelistv2.CustomWhitelistV2;
import org.sh0tix.customwhitelistv2.whitelist.CWV2Player;

import java.io.*;
import java.lang.reflect.Type;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlayerStatusHandler {
    
    /*
    This class will be used to handle the status of a player in the custom whitelist.
    The status of a player will be stored in a separate json file, located in a folder called "CustomWhitelistV2" in the plugins folder.
    The JSON file will be constructed as follows:
    {
        "playerUUID": "playerUUID", 
        {
            "playerName": "playerName",
            "status": "whitelisted",
            "lastUpdated": "2021-08-01T12:00:00Z",
            "numberOfWrongPasswordsEntered": 0,
            "numberOfTimesJoined": 5
        },
        "playerUUID": "playerUUID",
        {
            "playerName": "playerName",
            "status": "notWhitelisted",
            "lastUpdated": "2021-08-01T12:00:00Z",
            "numberOfWrongPasswordsEntered": 3,
            "numberOfTimesJoined": 0
        }
     }
     */

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Component.class, new ComponentTypeAdapter())
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .create();

    /**
     * Get the file where the player status is stored
     * @return The file where the player status is stored
     */
    public static File getFile() {
        File file = new File("plugins/CustomWhitelistV2/playerStatus.json");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
    
    /**
     * Insert a new player into the JSON file
     * @param player The player to insert into the JSON file
     */
    public static void insertNewPlayer(CWV2Player player) {
        File file = getFile();

        try {
            // First, read the existing players from the file
            List<CWV2Player> players = GSON.fromJson(new FileReader(file), new TypeToken<List<CWV2Player>>(){}.getType());

            // Check if players is null and initialize it if it is
            if (players == null) {
                players = new ArrayList<>();
            }

            players.add(player);

            // Then, create the BufferedWriter and write the updated list of players to the file
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            String json = GSON.toJson(players);
            bufferedWriter.write(json);
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Find a player by their UUID
     * @param playerUUID The UUID of the player to find
     * @return The player if they exist in the JSON file, null otherwise
     */
    public static CWV2Player FindPlayerByUUID(String playerUUID) {
        File file = getFile();

        try {
            List<CWV2Player> players = getCwv2PlayersList(file);

            // If there are no players in the JSON file, return null
            if (players == null) {
                return null;
            }

            for (CWV2Player player : players) {
                if (player.getUuid().equals(playerUUID)) {
                    return player;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Get all players from the JSON file
     * @return A list of all players
     */
    private static List<CWV2Player> getCwv2PlayersList(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        Type playerListType = new TypeToken<List<CWV2Player>>(){}.getType();
        List<CWV2Player> players = GSON.fromJson(bufferedReader, playerListType);
        bufferedReader.close();
        return players;
    }
    
    public static List<CWV2Player> getAllPlayers() {
        File file = getFile();

        try {
            return getCwv2PlayersList(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update the status of a player
     * @param playerUUID The UUID of the player to update
     * @param status The new status of the player
     */
    public static void updatePlayerStatus(String playerUUID, CWV2Player.Status status) {
        CWV2Player player = FindPlayerByUUID(playerUUID);
        if (player != null) {
            player.setStatus(status);
            player.setLastUpdated(new Date());
            updatePlayerInformationInFile(playerUUID, player);
        }
    }
    
    /**
     * Update the number of times a player has joined the server
     * @param playerUUID The UUID of the player to update
     */
    public static void updateNumberOfTimesJoined(String playerUUID) {
        CWV2Player player = FindPlayerByUUID(playerUUID);
        if (player != null) {
            player.setNumberOfTimesJoined(player.getNumberOfTimesJoined() + 1);
            updatePlayerInformationInFile(playerUUID, player);
        }
    }
    
    /**
     * Update the number of times a player has joined the server
     * @param playerUUID The UUID of the player to update
     * @param numberOfTimesJoined The new number of times the player has joined the server
     */
    public static void updateNumberOfTimesJoined(String playerUUID, int numberOfTimesJoined) {
        CWV2Player player = FindPlayerByUUID(playerUUID);
        if (player != null) {
            player.setNumberOfTimesJoined(numberOfTimesJoined);
            updatePlayerInformationInFile(playerUUID, player);
        }
    }
    
    /**
     * Update the number of wrong passwords entered by a player
     * @param playerUUID The UUID of the player to update
     */
    public static void updateNumberOfWrongPasswordsEntered(String playerUUID) {
        CWV2Player player = FindPlayerByUUID(playerUUID);
        if (player != null) {
            player.setNumberOfWrongPasswordsEntered(player.getNumberOfWrongPasswordsEntered() + 1);
            updatePlayerInformationInFile(playerUUID, player);
        }
    }
    
    /**
     * Update the number of wrong passwords entered by a player
     * @param playerUUID The UUID of the player to update
     * @param numberOfWrongPasswordsEntered The new number of wrong passwords entered by the player
     */
    public static void updateNumberOfWrongPasswordsEntered(String playerUUID, int numberOfWrongPasswordsEntered) {
        CWV2Player player = FindPlayerByUUID(playerUUID);
        if (player != null) {
            player.setNumberOfWrongPasswordsEntered(numberOfWrongPasswordsEntered);
            updatePlayerInformationInFile(playerUUID, player);
        }
    }
    
    /**
     * Update the player information in the JSON file
     * @param playerUUID The UUID of the player to update
     * @param updatedPlayerInformation The updated player information
     */
    private static void updatePlayerInformationInFile(String playerUUID, CWV2Player updatedPlayerInformation) {
        File file = getFile();

        try {
            List<CWV2Player> players = getCwv2PlayersList(file);

            // If there are no players in the JSON file, return null
            if (players == null) {
                return;
            }
            
            // Update the lastUpdated field in the player object
            updatedPlayerInformation.setLastUpdated(new Date(System.currentTimeMillis()));
            
            // Update the player information in the list
            for (CWV2Player player : players) {
                if (player.getUuid().equals(playerUUID)) {
                    player.setStatus(updatedPlayerInformation.getStatus());
                    player.setLastUpdated(updatedPlayerInformation.getLastUpdated());
                    player.setNumberOfWrongPasswordsEntered(updatedPlayerInformation.getNumberOfWrongPasswordsEntered());
                    player.setNumberOfTimesJoined(updatedPlayerInformation.getNumberOfTimesJoined());
                    player.setUsername(updatedPlayerInformation.getUsername());
                    if (player.getStatus() == CWV2Player.Status.TEMP_BANNED || player.getStatus() == CWV2Player.Status.TEMP_KICKED) {
                        player.setTempBannedOrKicked(updatedPlayerInformation.getDateOfUnbanOrUnkick(), updatedPlayerInformation.getReason());
                    }
                    
                }
            }

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            String json = GSON.toJson(players);
            bufferedWriter.write(json);
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Get all non-whitelisted players from the JSON file
     * @return A list of all non-whitelisted players
     */
    public static List<CWV2Player> getAllNonWhitelistedPlayers() {
        File file = getFile();

        try {
            return getCwv2PlayersList(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Get all whitelisted players from the JSON file
     * @return A list of all whitelisted players
     */
    public static String getPlayerUuidFromName(String name) {
        File file = getFile();

        try {
            List<CWV2Player> players = getCwv2PlayersList(file);

            // If there are no players in the JSON file, return null
            if (players == null) {
                return null;
            }
            
            for (CWV2Player player : players) {
                if (player.getUsername().equals(name)) {
                    return player.getUuid();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    
    /**
     * Get all whitelisted players from the JSON file
     * @return A list of all whitelisted players
     */
    private static Date generateExpiryDateFromTimeString(String timeString) {
        // The input string will be build like this:
        // Number + h, m, w, d, y
        
        try {
            int number = Integer.parseInt(timeString.substring(0, timeString.length() - 1));
            String timeUnit = timeString.substring(timeString.length() - 1);

            return switch (timeUnit) {
                case "s" -> Date.from(ZonedDateTime.now().plus(Duration.ofSeconds(number)).toInstant());
                case "h" -> Date.from(ZonedDateTime.now().plus(Duration.ofHours(number)).toInstant());
                case "m" -> Date.from(ZonedDateTime.now().plus(Duration.ofMinutes(number)).toInstant());
                case "w" -> Date.from(ZonedDateTime.now().plus(Duration.ofDays(number * 7)).toInstant());
                case "d" -> Date.from(ZonedDateTime.now().plus(Duration.ofDays(number)).toInstant());
                case "y" -> Date.from(ZonedDateTime.now().plus(Duration.ofDays(number * 365)).toInstant());
                default -> null;
            };
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generate a reason component from a string
     * @param newStatus The new status of the player
     * @param reasonString The reason for the new status
     * @param expiryDate The date the player can rejoin the server
     * @return A reason component
     */
    private static Component generateReasonComponentFromString(CWV2Player.Status newStatus, String reasonString, Date expiryDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        String formattedDate = localDateTime.format(formatter);
        
        return Component.text()
                .append(Component.text("You have been ", NamedTextColor.GRAY))
                .append(Component.text(newStatus.toString().toLowerCase().replace("_", " "), NamedTextColor.RED, TextDecoration.BOLD))
                .append(Component.text(" from this server by an administrator.\n", NamedTextColor.GRAY))
                .append(Component.text("\nReason: ", NamedTextColor.GRAY))
                .append(Component.text(reasonString + "\n", NamedTextColor.RED, TextDecoration.BOLD))
                .append(Component.text("\nYou can rejoin the server on ", NamedTextColor.GRAY))
                .append(Component.text(formattedDate, NamedTextColor.GREEN, TextDecoration.BOLD))
                .append(Component.text("\n\nIf you believe this is a mistake, please ", NamedTextColor.GRAY))
                .append(Component.text("contact an administrator.", NamedTextColor.BLUE, TextDecoration.UNDERLINED))
                .build();
    }
    
    /**
     * Set the status of a player to temp banned or temp kicked
     * @param player The player to set the status of
     * @param status The new status of the player
     * @param reasonString The reason for the new status
     * @param timeString The time the player is temp banned or temp kicked
     */
    public static void setPlayerIsTempBannedOrTempKicked(CWV2Player player, CWV2Player.Status status, String reasonString, String timeString) {
        File file = getFile();
        
        try {
            List<CWV2Player> players = getCwv2PlayersList(file);
            
            // If there are no players in the JSON file, return null
            if (players == null) {
                return;
            }
            
            for (CWV2Player p : players) {
                if (p.getUuid().equals(player.getUuid())) {
                    p.setStatus(status);
                    p.setTempBannedOrKicked(generateExpiryDateFromTimeString(timeString), generateReasonComponentFromString(status, reasonString, generateExpiryDateFromTimeString(timeString)));
                    
                    updatePlayerInformationInFile(p.getUuid(), p);
                }
            }
            
            // Debugging console message, if debug flag is set to true
            if (CustomWhitelistV2.debugMode) {
                CustomWhitelistV2.getInstance().getLogger().info("Player " + player.getUsername() + " has been " + status + " until " + generateExpiryDateFromTimeString(timeString));
                CustomWhitelistV2.getInstance().getLogger().info("Reason: " + generateReasonComponentFromString(status, reasonString, generateExpiryDateFromTimeString(timeString)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Get the reason for a player being temp banned or temp kicked
     * @param player The player to get the reason for
     * @return The reason for the player being temp banned or temp kicked
     */
    public static Component getTempBanOrTempKickMessage(CWV2Player player) {
        return player.getReason();
    }
    
    /**
     * Get the number of times a player has been temp banned or temp kicked
     * @param player The player to get the number of times temp banned or temp kicked
     * @return The number of times the player has been temp banned or temp kicked
     */
    public static Date getTempBanOrTempKickExpiryDate(CWV2Player player) {
        return player.getDateOfUnbanOrUnkick();
    }
}
