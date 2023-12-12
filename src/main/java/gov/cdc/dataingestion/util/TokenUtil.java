package gov.cdc.dataingestion.util;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.prefs.Preferences;

public class TokenUtil {
    private static final String NODE_NAME = "gov.cdc.dataingestion.util";
    private static final String TOKEN_KEY = "apiJwt";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String SECRET_KEY = "DICLIKeyDICLIKey";

    private Preferences preferences;

    public TokenUtil() {
        this.preferences = Preferences.userRoot().node(NODE_NAME);
    }

    public void storeToken(String token) {
//        System.out.println("token to store..."+ token);
        String encryptedToken = encryptToken(token);
//        preferences.put(TOKEN_KEY, token);
        preferences.put(TOKEN_KEY, encryptedToken);
    }

    private String encryptToken(String token) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(token.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String retrieveToken() {
//        System.out.println("token to retrieve..." + preferences.get(TOKEN_KEY,  null));
        String decryptedToken = decryptToken(preferences.get(TOKEN_KEY,  null));
//        return preferences.get(TOKEN_KEY,  null);
        return decryptedToken;
    }

    private String decryptToken(String encryptedToken) {
        if(encryptedToken != null) {
            try {
                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
                byte[] decodedBytes = Base64.getDecoder().decode(encryptedToken);
                byte[] decryptedBytes = cipher.doFinal(decodedBytes);
                return new String(decryptedBytes);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}