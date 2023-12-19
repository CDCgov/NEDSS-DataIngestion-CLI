package gov.cdc.dataingestion.util;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.prefs.Preferences;


public class TokenUtil {
    private static final String NODE_NAME = "gov.cdc.dataingestion.util";
    private static final String TOKEN_KEY = "apiJwt";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private String JWT_RANDOM_SALT;

    private Preferences preferences;

    public TokenUtil(String randomSalt) {
        this.preferences = Preferences.userRoot().node(NODE_NAME);
        this.JWT_RANDOM_SALT = randomSalt;
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
            byte[] iv = "iv".getBytes();
            SecretKey secretKey = new SecretKeySpec(JWT_RANDOM_SALT.getBytes(), ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
            byte[] encryptedBytes = cipher.doFinal(token.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            System.err.println("Exception Occurred: " + e.getMessage());
            return null;
        }
    }

    public String retrieveToken() {
        return decryptToken(preferences.get(TOKEN_KEY,  null));
    }

    private String decryptToken(String encryptedToken) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            byte[] iv = "iv".getBytes();
            SecretKey secretKey = new SecretKeySpec(JWT_RANDOM_SALT.getBytes(), ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedToken));
            return new String(decryptedBytes);
        } catch (Exception e) {
            System.err.println("Exception Occurred: " + e.getMessage());
            return null;
        }
    }
}