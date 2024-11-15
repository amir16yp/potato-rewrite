package potato;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static final Logger logger = new Logger(ConfigManager.class.getName());
    private static final String DEFAULT_CONFIG_PATH = "/potato/assets/config.properties";
    private static ConfigManager instance;
    private final Properties properties;

    private ConfigManager() {
        properties = new Properties();
        loadProperties();
    }

    public static ConfigManager get() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream input = ConfigManager.class.getResourceAsStream(DEFAULT_CONFIG_PATH)) {
            if (input == null) {
                logger.error("Configuration file not found in JAR: " + DEFAULT_CONFIG_PATH);
                useDefaultValues();
                return;
            }
            properties.load(input);
        } catch (IOException e) {
            logger.error("Failed to load configuration from JAR");
            logger.error(e);
            useDefaultValues();
        }
    }

    private void useDefaultValues() {
        logger.log("Using default values for all properties");
        for (GameProperty property : GameProperty.values()) {
            // Store default values as Strings for consistency
            properties.setProperty(property.getKey(), String.valueOf(property.getDefaultValue()));
        }
    }

    public int getInt(GameProperty property) {
        return getPropertyValue(property, Integer::parseInt, property.getDefaultValue());
    }

    public double getDouble(GameProperty property) {
        return getPropertyValue(property, Double::parseDouble, property.getDefaultValue());
    }

    public long getLong(GameProperty property) {
        return getPropertyValue(property, Long::parseLong, property.getDefaultValue());
    }

    public boolean getBoolean(GameProperty property) {
        String value = properties.getProperty(property.getKey());
        if (value == null) {
            return getBooleanDefault(property);
        }
        return Boolean.parseBoolean(value);
    }

    private boolean getBooleanDefault(GameProperty property) {
        Object defaultValue = property.getDefaultValue();
        if (defaultValue instanceof Boolean) {
            return (Boolean) defaultValue;
        }
        logger.error("Expected Boolean default value for property: " + property.getKey());
        return false; // Default to false in case of an error
    }

    // General method to get property values and handle errors
    private <T> T getPropertyValue(GameProperty property, PropertyParser<T> parser, Object defaultValue) {
        String value = properties.getProperty(property.getKey());
        if (value == null) {
            return castDefaultValue(defaultValue);
        }
        try {
            return parser.parse(value);
        } catch (Exception e) {
            logger.error("Invalid value for property: " + property.getKey());
            return castDefaultValue(defaultValue);
        }
    }

    // Cast default value to the appropriate type
    private <T> T castDefaultValue(Object defaultValue) {
        if (defaultValue instanceof Integer) {
            return (T) defaultValue;  // Cast Integer
        } else if (defaultValue instanceof Long) {
            return (T) defaultValue;  // Cast Long
        } else if (defaultValue instanceof Double) {
            return (T) defaultValue;  // Cast Double
        } else if (defaultValue instanceof Boolean) {
            return (T) defaultValue;  // Cast Boolean
        } else {
            throw new IllegalArgumentException("Unknown default value type: " + defaultValue.getClass());
        }
    }

    // Reload the configuration from the properties file
    public void reload() {
        loadProperties();
    }

    // Functional interface for parsing property values
    private interface PropertyParser<T> {
        T parse(String value) throws Exception;
    }
}
