package gov.cdc.dataingestion.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class TokenUtilTest {
    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outStream));
    }

    @Test
    void testStoreAndRetrieveToken() {
        String originalToken = "testToken";
        String validRandomSalt = "random_Salt_Test";
        TokenUtil tokenUtil = new TokenUtil(validRandomSalt);

        tokenUtil.storeToken(originalToken);

        String retrievedToken = tokenUtil.retrieveToken();
        assertEquals(originalToken, retrievedToken);
    }

    @Test
    void testStoreTokenWithInvalidEncryption() {
        String originalToken = "testToken";
        String invalidRandomSalt = "someRandomSaltForStoreToken";
        TokenUtil tokenUtil = new TokenUtil(invalidRandomSalt);
        String expectedOutput = "Exception Occurred: Invalid AES key length: 27 bytes\n" +
                "Encryption failed for JWT.";

        tokenUtil.storeToken(originalToken);

        assertEquals(expectedOutput, outStream.toString().trim());
    }

    @Test
    void testRetrieveTokenWithInvalidEncryption() {
        String invalidRandomSalt = "someRandomSaltForRetrieveToken";
        TokenUtil tokenUtil = new TokenUtil(invalidRandomSalt);
        String expectedOutput = "Exception Occurred: Invalid AES key length: 30 bytes";

        tokenUtil.retrieveToken();

        assertEquals(expectedOutput, outStream.toString().trim());
    }
}