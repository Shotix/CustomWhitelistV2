package org.sh0tix.customwhitelistv2.web;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.sh0tix.customwhitelistv2.handlers.PlayerStatusHandler;
import org.sh0tix.customwhitelistv2.whitelist.CWV2Player;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class WebServer {
    private HttpServer server;
    
    public void startServer() {
        // Start the web server
        try {
            server = HttpServer.create(new InetSocketAddress(7777), 0);
            server.createContext("/", new MyHandler());
            server.createContext("/api/players", new PlayerHandler());
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