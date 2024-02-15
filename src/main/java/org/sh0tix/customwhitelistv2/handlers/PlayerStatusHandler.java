package org.sh0tix.customwhitelistv2.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.sh0tix.customwhitelistv2.whitelist.CWV2Player;

import java.io.*;
import java.lang.reflect.Type;
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
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .create();
        
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            List<CWV2Player> players = gson.fromJson(new FileReader(file), new TypeToken<List<CWV2Player>>(){}.getType());

            // Check if players is null and initialize it if it is
            if (players == null) {
                players = new ArrayList<>();
            }

            players.add(player);
            String json = gson.toJson(players);
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
        Gson gson = new Gson();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Type playerListType = new TypeToken<List<CWV2Player>>(){}.getType();
            List<CWV2Player> players = gson.fromJson(bufferedReader, playerListType);
            bufferedReader.close();

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
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .create();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Type playerListType = new TypeToken<List<CWV2Player>>(){}.getType();
            List<CWV2Player> players = gson.fromJson(bufferedReader, playerListType);
            bufferedReader.close();
            
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
                    player.setName(updatedPlayerInformation.getName());
                }
            }

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            String json = gson.toJson(players);
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
        Gson gson = new Gson();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Type playerListType = new TypeToken<List<CWV2Player>>(){}.getType();
            
            List<CWV2Player> playersList = gson.fromJson(bufferedReader, playerListType);
            bufferedReader.close();
            return playersList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
