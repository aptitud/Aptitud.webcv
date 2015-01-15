package se.webcv.utils;

import java.util.function.Supplier;

/**
 * Created by marcus on 15/01/15.
 */
public interface ConfigUtils {
    static String systemOrEnv(String key, Supplier<String> defaultValue) {
        String value = System.getProperty(key);
        if (value != null) {
            return value;
        }
        value = System.getenv(key);
        if (value != null) {
            return value;
        }
        return defaultValue.get();
    }
}
