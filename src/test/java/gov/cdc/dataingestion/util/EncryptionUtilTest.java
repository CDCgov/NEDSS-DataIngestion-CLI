package gov.cdc.dataingestion.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionUtilTest {
    private static final String TOKEN_KEY = "apiJwt";
    private static final String CLIENT_ID_KEY = "clientId";
    private static final String CLIENT_SECRET_KEY = "clientSecret";
    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outStream));
    }

    @Test
    void testStoreAndRetrieveToken() {
        String originalToken = "testToken";
        String validRandomSalt = "random_Salt_Test";
        EncryptionUtil encryptionUtil = new EncryptionUtil(validRandomSalt);

        encryptionUtil.storeString(originalToken, TOKEN_KEY);

        String retrievedToken = encryptionUtil.retrieveString(TOKEN_KEY);
        assertEquals(originalToken, retrievedToken);
    }

    @Test
    void testStoreTokenWithInvalidEncryption() {
        String originalToken = "testToken";
        String invalidRandomSalt = "someRandomSaltForStoreToken";
        EncryptionUtil encryptionUtil = new EncryptionUtil(invalidRandomSalt);
        String expectedOutput = "Exception occurred during encryption: Invalid AES key length: 27 bytes\n" +
                "Encryption failed for given data.";

        encryptionUtil.storeString(originalToken, TOKEN_KEY);

        assertEquals(expectedOutput, outStream.toString().trim());
    }

    @Test
    void testRetrieveTokenWithInvalidEncryption() {
        String invalidRandomSalt = "someRandomSaltForRetrieveToken";
        EncryptionUtil encryptionUtil = new EncryptionUtil(invalidRandomSalt);
        String expectedOutput = "Exception occurred during decryption: Invalid AES key length: 30 bytes";

        encryptionUtil.retrieveString(TOKEN_KEY);

        assertEquals(expectedOutput, outStream.toString().trim());
    }
}