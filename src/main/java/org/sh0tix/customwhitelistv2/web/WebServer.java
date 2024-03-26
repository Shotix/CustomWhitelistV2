package org.sh0tix.customwhitelistv2.web;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.Bukkit;
import org.sh0tix.customwhitelistv2.handlers.PasswordHandler;
import org.sh0tix.customwhitelistv2.handlers.PlayerStatusHandler;
import org.sh0tix.customwhitelistv2.whitelist.CWV2Player;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebServer {
    private HttpServer server;
    
    public void startServer() {
        // Start the web server
        try {
            server = HttpServer.create(new InetSocketAddress(7777), 0);
            server.createContext("/", new MyHandler());
            server.createContext("/api/players", new PlayerHandler());
            server.createContext("/api/updatePassword", new UpdatePasswordHandler());
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

    static class UpdatePasswordHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            // Read the request body
            InputStreamReader isr = new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();

            // Parse the current and new password from the request body
            Map<String, String> parameters = parseFormData(query);
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

        private Map<String, String> parseFormData(String formData) {
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
    }
    

    static class PlayerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            List<CWV2Player> players = PlayerStatusHandler.getAllPlayers(); // Replace with your method to get players
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