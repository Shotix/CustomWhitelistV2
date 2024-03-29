package org.sh0tix.customwhitelistv2.web;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.sh0tix.customwhitelistv2.CustomWhitelistV2;
import org.sh0tix.customwhitelistv2.handlers.PasswordHandler;
import org.sh0tix.customwhitelistv2.handlers.PlayerStatusHandler;
import org.sh0tix.customwhitelistv2.handlers.WhitelistHandler;
import org.sh0tix.customwhitelistv2.whitelist.CWV2Player;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class WebServer {
    private HttpServer server;
    
    public void startServer() {
        // Start the web server
        try {
            server = HttpServer.create(new InetSocketAddress(7777), 0);
            server.createContext("/", new MyHandler());
            server.createContext("/api/getDebugMode", new GetDebugModeHandler());
            server.createContext("/api/players", new PlayerHandler());
            server.createContext("/api/updatePassword", new UpdatePasswordHandler());
            server.createContext("/api/players/", new PlayerDetailsHandler());
            server.createContext("/api/updatePlayerStatus", new UpdatePlayerStatusHandler());
            server.createContext("/api/getLocalization", new GetSelectedLanguage());
            server.createContext("/api/setDebugMode", new SetDebugModeHandler());
            server.createContext("/api/setLocalization", new SetPluginLanguage());
            server.createContext("/api/checkForCorrectTempPlayerStatuses", new CheckForCorrectTempPlayerStatuses());
            server.setExecutor(null); // creates a default executor
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void stopServer() {
        // Stop the web server
        if (server != null) {
            server.stop(0);
        }
    }
    
    static class CheckForCorrectTempPlayerStatuses implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            // Get all players
            List<CWV2Player> players = PlayerStatusHandler.getAllPlayers();
            
            // Check if any player has a temporary status that has expired
            for (CWV2Player player : players) {
                PlayerStatusHandler.checkAndUpdatePlayerStatusIfTempBanOrKickIsExpired(player);
            }
        }
    }
    
    /**
     * Parse the form data from a POST request
     * @param formData The form data
     * @return A map with the form data
     */
    public static Map<String, String> parseFromData(String formData) {
        Map<String, String> parameters = new HashMap<>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length > 1) {
                String key = keyValue[0];
                String value = keyValue[1];
                parameters.put(key, value);
            } else {
                parameters.put(keyValue[0], "");
            }
        }

        return parameters;
    }
    
    static class SetPluginLanguage implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            // Read the request body
            InputStreamReader isr = new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();

            // Parse the debug mode from the request body
            Map<String, String> parameters = parseFromData(query);

            // Check if the parameter contains a valid language (de_DE, en_US)
            String language = parameters.get("localization");
            String response;
            if (language.equals("German") || language.equals("English")) {
                CustomWhitelistV2.getLocalizationHandler().saveSelectedLanguage(language.equals("German") ? "de_DE" : "en_US");
                CustomWhitelistV2.getLocalizationHandler().loadLocalization(language.equals("German") ? "de_DE" : "en_US");
                response = new Gson().toJson(true);
            } else {
                // Invalid response
                response = new Gson().toJson(false);
            }
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();
            return;
        }
    }
    
    static class SetDebugModeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            // Read the request body
            InputStreamReader isr = new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            
            // Parse the debug mode from the request body
            Map<String, String> parameters = parseFromData(query);
            
            // Check if the parameter contains a valid debug mode (Enabled, Disabled)
            boolean debugMode = false;
            if (Objects.equals(parameters.get("debugMode"), "Enabled")) {
                debugMode = true;
            } else if (parameters.get("debugMode") == null) {
                // Invalid response
                String response = new Gson().toJson(false);
                byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                t.sendResponseHeaders(200, bytes.length);
                OutputStream os = t.getResponseBody();
                os.write(bytes);
                os.close();
                return;
            }
            
            // Set the debug mode
            CustomWhitelistV2.setDebugMode(debugMode);
            
            String response = new Gson().toJson(true);
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }
    
    static class GetSelectedLanguage implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = new Gson().toJson(CustomWhitelistV2.getSelectedLanguage());
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

    static class GetDebugModeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = new Gson().toJson(CustomWhitelistV2.getDebugMode());
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }
    
    static class UpdatePlayerStatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            // Read the request body
            InputStreamReader isr = new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            
            // Parse the player status from the request body
            Map<String, String> parameters = parseFromData(query);
            
            // Get the players UUID from the username
            String UUID = PlayerStatusHandler.getPlayerUuidFromName(parameters.get("username"));
            
            // Get the current player
            CWV2Player player = PlayerStatusHandler.getAllPlayers().stream()
                    .filter(p -> p.getUuid().equals(UUID))
                    .findFirst()
                    .orElse(null);
            
            // Update the player status
            PlayerStatusHandler.updatePlayerStatus(UUID, CWV2Player.Status.valueOf(parameters.get("status")));
            
            Player playerOnline = Bukkit.getPlayer(parameters.get("username"));

            // Get the duration of the status get the first letter duration
            String duration = parameters.get("duration") + parameters.get("unit").charAt(0);

            String reason = parameters.get("reason");

            PlayerStatusHandler.setPlayerIsTempBannedOrTempKicked(player, CWV2Player.Status.valueOf(parameters.get("status")), reason, duration);
            
            if (playerOnline != null) {
                WhitelistHandler.disablePlayerMovementAndSight(playerOnline);

                assert player != null;
                Component kickMessage = PlayerStatusHandler.getTempBanOrTempKickMessage(player);
                
                playerOnline.kick(kickMessage);
            }
            
            // Prepare the response
            String response = new Gson().toJson(player);
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            
            // Send the response
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

    static class PlayerDetailsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String path = t.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length < 4) {
                // The path does not include a username, return a 400 Bad Request response
                String response = "400 (Bad Request)\n";
                t.sendResponseHeaders(400, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }

            String username = parts[3];
            // Replace with your method to get a player by username
            List<CWV2Player> players = PlayerStatusHandler.getAllPlayers();
            CWV2Player player = players.stream()
                    .filter(p -> p.getUsername().equals(username))
                    .findFirst()
                    .orElse(null);

            if (player == null) {
                // The player does not exist, return a 404 Not Found response
                String response = "404 (Not Found)\n";
                t.sendResponseHeaders(404, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }

            // The player exists, return a 200 OK response with the player data
            String response = new Gson().toJson(player);
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

    static class UpdatePasswordHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            // Read the request body
            InputStreamReader isr = new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();

            // Parse the current and new password from the request body
            Map<String, String> parameters = parseFromData(query);
            String currentPassword = parameters.get("currentPassword");
            String newPassword = parameters.get("newPassword");

            // Check the current password
            boolean isCorrect = PasswordHandler.checkPassword(currentPassword);

            // If the current password is correct, update the password
            if (isCorrect) {
                PasswordHandler.updatePassword(newPassword);
            }

            // Prepare the response
            String response = new Gson().toJson(isCorrect);
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

            // Send the response
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }
    
    static class PlayerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            List<CWV2Player> players = PlayerStatusHandler.getAllPlayers();
            String response = new Gson().toJson(players);
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }
    
    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String root = "plugins/CustomWhitelistV2/web";
            URI uri = t.getRequestURI();
            String path = uri.getPath();
            
            // If the path is empty, we assume the user wants the index.html file
            if (path.equals("/")) {
                path = "/index.html";
            }
            
            File rootDir = new File(root).getCanonicalFile();
            File requestedFile = new File(root + path).getCanonicalFile();

            if (!requestedFile.getPath().startsWith(rootDir.getPath())) {
                // Suspected path traversal attack: reject with 403 error.
                String response = "403 (Forbidden)\n";
                t.sendResponseHeaders(403, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else if (!requestedFile.isFile()) {
                // Object does not exist or is not a file: reject with 404 error.
                String response = "404 (Not Found)\n";
                t.sendResponseHeaders(404, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                // Object exists and is a file: accept with response code 200.
                t.sendResponseHeaders(200, 0);
                OutputStream os = t.getResponseBody();
                Files.copy(requestedFile.toPath(), os);
                os.close();
            }
        }
    }
}