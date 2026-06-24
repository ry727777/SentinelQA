package com.rahul.framework.config;

import com.rahul.framework.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Reads configuration properties from config.properties file.
 * Provides methods to retrieve configuration values as key-value pairs.
 */
public class ConfigReader {
    private static final Logger logger = LoggerUtil.getLogger(ConfigReader.class);
    private static final String CONFIG_FILE_PATH = "src/main/resources/config.properties";
    private static Properties properties;

    static {
        loadProperties();
    }

    /**
     * Get a configuration value by key.
     *
     * @param key the configuration key
     * @return the configuration value, or null if key not found
     */
    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            logger.warn("Property key '{}' not found in config.properties", key);
        }
        return value;
    }

    /**
     * Get a configuration value by key with a default value.
     *
     * @param key          the configuration key
     * @param defaultValue the default value if key not found
     * @return the configuration value, or defaultValue if key not found
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Get an integer configuration value.
     *
     * @param key the configuration key
     * @return the integer value
     */
    public static int getIntProperty(String key) {
        String value = getProperty(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.error("Property '{}' with value '{}' is not a valid integer", key, value);
            throw new RuntimeException("Invalid integer property: " + key, e);
        }
    }

    /**
     * Get a boolean configuration value.
     *
     * @param key the configuration key
     * @return the boolean value
     */
    public static boolean getBooleanProperty(String key) {
        String value = getProperty(key);
        return Boolean.parseBoolean(value);
    }

    /**
     * Load or reload the properties file.
     * Can be called to refresh configuration.
     */
    public static void loadProperties() {
        logger.info("Loading properties from: " + CONFIG_FILE_PATH);
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(fis);
            logger.info("Properties loaded successfully");
        } catch (IOException e) {
            logger.error("Failed to load config.properties: {}", e.getMessage());
            throw new RuntimeException("Could not load config.properties from " + CONFIG_FILE_PATH, e);
        }
    }
}