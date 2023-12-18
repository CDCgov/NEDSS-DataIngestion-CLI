package gov.cdc.dataingestion.util;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.prefs.Preferences;

public class TokenUtil {
    private static final String NODE_NAME = "gov.cdc.dataingestion.util";
    private static final String TOKEN_KEY = "apiJwt";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    public static String JWT_RANDOM_SALT = "DICLI_RandomSalt";

    private Preferences preferences;

    public TokenUtil() {
        this.preferences = Preferences.userRoot().node(NODE_NAME);
    }

    public void storeToken(String token) {
        String encryptedToken = encryptToken(token);
        if(encryptedToken != null) {
            preferences.put(TOKEN_KEY, encryptedToken);
        }
        else {
            System.err.println("Encryption failed for JWT.");
        }
    }

    private String encryptToken(String token) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKey secretKey = new SecretKeySpec(JWT_RANDOM_SALT.getBytes(), ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(token.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            System.err.println("Exception Occurred: " + e.getMessage());
            return null;
        }
    }

    public String retrieveToken() {
        String decryptedToken = decryptToken(preferences.get(TOKEN_KEY,  null));
        return decryptedToken;
    }

    private String decryptToken(String encryptedToken) {
        if(encryptedToken != null) {
            try {
                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                SecretKey secretKey = new SecretKeySpec(JWT_RANDOM_SALT.getBytes(), ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
                byte[] decodedBytes = Base64.getDecoder().decode(encryptedToken);
                byte[] decryptedBytes = cipher.doFinal(decodedBytes);
                return new String(decryptedBytes);
            } catch (Exception e) {
                System.err.println("Exception Occurred: " + e.getMessage());
                return null;
            }
        }
        return null;
    }
}