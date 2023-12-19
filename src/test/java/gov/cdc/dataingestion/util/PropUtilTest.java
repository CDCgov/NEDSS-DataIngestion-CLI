package gov.cdc.dataingestion.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class PropUtilTest {

    private PropUtil propUtil;
    Properties propertiesMock;
    private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();


    @BeforeEach
    void setUp() {
        System.setErr(new PrintStream(errStream));
        propUtil = new PropUtil();
    }

    @Test
    void testLoadPropertiesFileSuccess() {
        propUtil.propertiesFileName = "test.config.properties";
        propertiesMock = propUtil.loadPropertiesFile();

        assertNotNull(propertiesMock);
        assertFalse(propertiesMock.isEmpty());

        assertEquals(propertiesMock.getProperty("service.registrationEndpoint"), "testRegistrationEndpoint");
        assertEquals(propertiesMock.getProperty("service.tokenEndpoint"), "testTokenEndpoint");
        assertEquals( propertiesMock.getProperty("service.reportsEndpoint"), "testReportsEndpoint");
    }

    @Test
    void testLoadPropertiesFileEmptyProperties() {
        propUtil.propertiesFileName = "test.empty.config.properties";
        propertiesMock = propUtil.loadPropertiesFile();

        assertTrue(propertiesMock.isEmpty());

    }

    @Test
    void testLoadPropertiesFileNotFound() {
        propUtil.propertiesFileName = "config.not.exists.properties";

        assertThrows(NullPointerException.class, propUtil::loadPropertiesFile);

        String expectedOutput = "Unable to load config.properties. Please check the err trace.";
        assertEquals(expectedOutput, errStream.toString().trim());
        assertNull(propertiesMock);
    }
}