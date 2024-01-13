package gov.cdc.dataingestion.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.*;

class PropUtilTest {

    private PropUtil propUtil;
    private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();


    @BeforeEach
    void setUp() {
        System.setErr(new PrintStream(errStream));
        propUtil = new PropUtil("test.config.properties");

    }

    @Test
    void testGetProperty() {
        String result = propUtil.getProperty("service.tokenEndpoint");
        assertEquals("testTokenEndpoint", result);
    }

    @Test
    void testLoadPropertiesFileNotFound() {
        propUtil.properties = new Properties();
        propUtil.propertiesFileName = "config.not.exists.properties";
        assertThrows(NullPointerException.class, propUtil::loadPropertiesFile);
        String expectedOutput = "Unable to load config.properties. Please check the err trace.";
        assertEquals(expectedOutput, errStream.toString().trim());
    }

}