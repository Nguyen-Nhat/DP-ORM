package org.group05.com;

import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManager {
    private ConfigurationManager() {
        loadProperties();
    }

    private static ConfigurationManager instance = null;

    public static ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    private Properties properties;

    public void loadProperties() {
        properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (inputStream == null) {
                throw new IllegalArgumentException("application.properties not found in classpath");
            }

            assert properties != null;
            properties.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
