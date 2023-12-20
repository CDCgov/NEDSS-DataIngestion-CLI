package gov.cdc.dataingestion.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;


public class TokenUtil {
    private static final Logger logger = Logger.getLogger(TokenUtil.class.getName());
    private static final String NODE_NAME = "gov.cdc.dataingestion.util";
    private static final String TOKEN_KEY = "apiJwt";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private String jwtRandomSalt;

    private Preferences preferences;

    public TokenUtil(String randomSalt) {
        this.preferences = Preferences.userRoot().node(NODE_NAME);
        this.jwtRandomSalt = randomSalt;
    }

    public void storeToken(String token) {
        String encryptedToken = encryptToken(token);
        if(encryptedToken != null) {
            preferences.put(TOKEN_KEY, encryptedToken);
        }
        else {
            // Updating this to logger for now but has to be cleaned up to use Mixin from parent class
            // System.err.println("Encryption failed for JWT.");
            logger.log(Level.SEVERE, "Encryption failed for JWT.");

        }
    }

    private String encryptToken(String token) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKey secretKey = new SecretKeySpec(jwtRandomSalt.getBytes(), ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] iv = cipher.getIV();
            byte[] encryptedBytes = cipher.doFinal(token.getBytes());
            byte[] combined = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, combined,  iv.length, encryptedBytes.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            // Updating this to logger for now but has to be cleaned up to use Mixin from parent class
            // System.err.println("Exception Occurred: " + e.getMessage());
            logger.log(Level.WARNING, "Exception Occurred: " + e.getMessage());
            return null;
        }
    }

    public String retrieveToken() {
        return decryptToken(preferences.get(TOKEN_KEY,  null));
    }

    private String decryptToken(String encryptedToken) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKey secretKey = new SecretKeySpec(jwtRandomSalt.getBytes(), ALGORITHM);
            byte[] combined = Base64.getDecoder().decode(encryptedToken);
            byte[] iv = new byte[12];
            System.arraycopy(combined, 0, iv, 0, 12);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
            byte[] decryptedBytes = cipher.doFinal(combined, 12, combined.length - 12);
            return new String(decryptedBytes);
        } catch (Exception e) {
            // Updating this to logger for now but has to be cleaned up to use Mixin from parent class
            // System.err.println("Exception Occurred: " + e.getMessage());
            logger.log(Level.WARNING, "Exception Occurred: " + e.getMessage());
            return null;
        }
    }
}