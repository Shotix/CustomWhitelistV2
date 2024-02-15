package org.sh0tix.customwhitelistv2.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;
import org.sh0tix.customwhitelistv2.whitelist.CWV2Player;
import org.sh0tix.customwhitelistv2.whitelist.Password;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class PasswordHandler {

    private static final String SALT_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final int SALT_LENGTH = 8;
    
    /*
    This class will handle the password for the custom whitelist.
    The password will be stored in a separate json file, located in a folder called "CustomWhitelistV2" in the plugins folder.
    The password will be hashed and salted using the SHA-256 algorithm.
     */

    /**
     * Get the file where the password is stored
     * @return The file where the password is stored
     */
    private static File getFile() {
        File file = new File("plugins/CustomWhitelistV2/password.json");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
                
                initialPasswordCreation();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * Create the initial password and store it in the JSON file
     */
    private static void initialPasswordCreation(){
        File file = getFile();
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));

            // Create initial password
            String salt = generateSalt();
            String hashedPassword = generateSaltedHashFromString("password", salt);
            Password password = new Password("joinPassword", hashedPassword, salt);

            // Write password to JSON file
            String json = gson.toJson(password);
            bufferedWriter.write(json);
            bufferedWriter.close();
        } catch (IOException | NoSuchAlgorithmException e) {
            System.err.println("Error creating initial password: " + e.getMessage());
        }
    }

    /**
     * Update the password in the JSON file
     * @param newPassword The new password
     * @return True if the password was updated successfully, false otherwise
     */
    public static boolean updatePassword(String newPassword) {
        File file = getFile();
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        try {
            // Generate new salt and hash the new password
            String newSalt = generateSalt();
            String newHashedPassword = generateSaltedHashFromString(newPassword, newSalt);

            // Create a new password
            Password password = new Password("joinPassword", newHashedPassword, newSalt);

            // Write the updated password to the JSON file
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            String json = gson.toJson(password);
            bufferedWriter.write(json);
            bufferedWriter.close();

            return true;
        } catch (IOException | NoSuchAlgorithmException e) {
            System.err.println("Error updating password: " + e.getMessage());
        }

        return false;
    }

    /**
     * Check if the input password is correct
     * @param inputPassword The input password
     * @return True if the input password is correct, false otherwise
     */
    public static boolean checkPassword(String inputPassword) {
        File file = getFile();
        Gson gson = new Gson();
        
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Password password = gson.fromJson(bufferedReader, Password.class);
            
            // Generate the hash from the input password and the salt from the JSON file
            String hashedInputPassword = generateSaltedHashFromString(inputPassword, password.getSalt());
            
            // Compare the generated hash with the hash from the JSON file
            return Objects.equals(hashedInputPassword, password.getPassword());
        } catch (IOException | NoSuchAlgorithmException e) {
            System.err.println("Error checking password: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Generate a salted hash from a string
     * @param inputPassword The input password
     * @param salt The salt
     * @return The salted hash
     * @throws NoSuchAlgorithmException If the SHA-256 algorithm is not found
     */
    private static String generateSaltedHashFromString(String inputPassword, String salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedInputPassword = digest.digest((inputPassword + salt).getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder(2 * encodedInputPassword.length);
        for (byte b : encodedInputPassword) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Generate a random salt
     * @return The random salt
     */
    private static String generateSalt() {
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < SALT_LENGTH) {
            int index = (int) (rnd.nextFloat() * SALT_CHARACTERS.length());
            salt.append(SALT_CHARACTERS.charAt(index));
        }
        return salt.toString();
    }
}
