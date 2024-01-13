//package gov.cdc.dataingestion.config;
//
//import gov.cdc.dataingestion.util.PropUtil;
//
//import java.util.Properties;
//
//public class AppConfig {
//    private Properties properties;
//    public PropUtil propUtil = new PropUtil(); //NOSONAR
//
//    public AppConfig() {
//        this.properties = new Properties();
//        loadProperties();
//    }
//
//    private void loadProperties() {
//        properties = propUtil.loadPropertiesFile();
//        String envVarValue = System.getenv("DI_URL");
//        if (envVarValue != null) {
//            properties.setProperty("service.env.url", envVarValue);
//        }
//
//    }
//
//    public String getProperty(String key) {
//        return properties.getProperty(key);
//    }
//}
