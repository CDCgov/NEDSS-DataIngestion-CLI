package gov.cdc.dataingestion.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.prefs.Preferences;


public class EncryptionUtil {
    private static final String NODE_NAME = "gov.cdc.dataingestion.util";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private String jwtRandomSalt;

    private Preferences preferences;

    public EncryptionUtil(String randomSalt) {
        this.preferences = Preferences.userRoot().node(NODE_NAME);
        this.jwtRandomSalt = randomSalt;
    }

    @SuppressWarnings("java:S106")
    public void storeString(String token, String key) {
        String encryptedString = encryptString(token, key);
        if(encryptedString != null) {
            preferences.put(key, encryptedString);
        }
        else {
            System.out.println("Encryption failed for given data.");
        }
    }

    @SuppressWarnings("java:S106")
    private String encryptString(String data, String key) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKey secretKey = new SecretKeySpec(jwtRandomSalt.getBytes(), ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] iv = cipher.getIV();
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            byte[] combined = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, combined,  iv.length, encryptedBytes.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            System.out.println("Exception occurred during encryption: " +e.getMessage());
            return null;
        }
    }

    public String retrieveString(String key) {
        return decryptString(preferences.get(key,  null), key);
    }

    @SuppressWarnings("java:S106")
    private String decryptString(String encryptedData, String key) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            byte[] combined = Base64.getDecoder().decode(encryptedData);
            byte[] ivFromCombined = Arrays.copyOfRange(combined, 0, 12);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, ivFromCombined);
            SecretKey secretKey = new SecretKeySpec(jwtRandomSalt.getBytes(), ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
            byte[] decryptedBytes = cipher.doFinal(combined, 12, combined.length - 12);
            return new String(decryptedBytes);
        } catch (Exception e) {
            System.out.println("Exception occurred during decryption: " +e.getMessage());
            return null;
        }
    }
}