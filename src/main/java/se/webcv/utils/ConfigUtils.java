package se.webcv.utils;

/**
 * Created by marcus on 15/01/15.
 */
public class ConfigUtils {
    private ConfigUtils() {}
    public static String systemOrEnv(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value != null) {
            return value;
        }
        value = System.getenv(key);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }
}
