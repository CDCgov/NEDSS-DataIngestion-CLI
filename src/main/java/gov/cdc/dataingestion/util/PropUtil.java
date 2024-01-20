package gov.cdc.dataingestion.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropUtil {

    protected String propertiesFileName = "config.properties";
    protected Properties properties = new Properties();
    private static final String DI_URL = "DI_URL";

    public PropUtil() {
        loadProperties();
    }

    //Constructor for Unit Test
    public PropUtil(String testProperty) {
        this.propertiesFileName = testProperty;
        loadProperties();
    }


    protected Properties loadPropertiesFile() {
        InputStream propertiesInput = PropUtil.class.getClassLoader().getResourceAsStream(propertiesFileName);
        try {
            if (propertiesInput == null) {
                System.err.println("Unable to load config.properties. Please check the err trace.");
            }
            properties.load(propertiesInput);
        } catch (IOException e) {
            System.err.println("Exception occurred: " + e.getMessage());
        }
        return properties;
    }

    protected void loadProperties() {
        properties = loadPropertiesFile();
        String envVarValue = System.getenv(DI_URL);
        if (envVarValue != null) {
            properties.setProperty("service.env.url", envVarValue);
        }

    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
