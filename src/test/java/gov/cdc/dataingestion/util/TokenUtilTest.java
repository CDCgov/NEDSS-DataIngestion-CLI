package gov.cdc.dataingestion.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class TokenUtilTest {
    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outStream));
        System.setErr(new PrintStream(errStream));

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

        tokenUtil.storeToken(originalToken);

        assertFalse(errStream.toString().trim().contains("Encryption failed for JWT."));
    }

    @Test
    void testRetrieveTokenWithInvalidEncryption() {
        String invalidRandomSalt = "someRandomSaltForRetrieveToken";
        TokenUtil tokenUtil = new TokenUtil(invalidRandomSalt);

        tokenUtil.retrieveToken();

        assertTrue(errStream.toString().trim().contains("Invalid AES key length: 30 bytes"));
    }
}