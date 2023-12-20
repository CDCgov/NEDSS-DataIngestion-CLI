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
}