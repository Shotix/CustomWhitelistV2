package org.sh0tix.customwhitelistv2.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class LocalizationHandler {
    private final JavaPlugin plugin;
    private FileConfiguration localization;
    public LocalizationHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public String getLocalisedString(String key) {
        return localization.getString(key);
    }

    public void saveSelectedLanguage(String languageCode) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("selectedLanguage", languageCode);

        try (FileWriter file = new FileWriter(plugin.getDataFolder() + "/localization/status.json")) {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            file.write(gson.toJson(jsonObject));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String loadSelectedLanguage() {
        File statusFile = new File(plugin.getDataFolder() + "/localization/status.json");
        if (!statusFile.exists()) {
            saveSelectedLanguage("en_US");
        }

        try (FileReader reader = new FileReader(statusFile)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            return jsonObject.get("selectedLanguage").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "en_US"; // default language
    }

    public void loadLocalization(String languageCode) {
        File localizationFile = new File(plugin.getDataFolder(), "localization/" + languageCode + ".yml");
        if (localizationFile.exists()) {
            localization = YamlConfiguration.loadConfiguration(localizationFile);
        } else {
            plugin.getLogger().warning("Localization file for language code " + languageCode + " not found! Falling back to en_US.yml...");
            localizationFile = new File(plugin.getDataFolder(), "localization/en_US.yml");
            localization = YamlConfiguration.loadConfiguration(localizationFile);
        }
    }

    public void saveDefaultLocalizationFile(String fileName) {
        File localizationFolder = new File(plugin.getDataFolder(), "localization");
        if (!localizationFolder.exists()) {
            localizationFolder.mkdirs();
        }

        File localizationFile = new File(localizationFolder, fileName);
        if (!localizationFile.exists()) {
            plugin.saveResource("localization/" + fileName, false);
        }
    }
}
