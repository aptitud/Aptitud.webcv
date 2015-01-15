package se.webcv.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigUtilsTest {

    @Test
    public void shouldGetSystem() {
        System.setProperty("dummy", "value");
        assertEquals(ConfigUtils.systemOrEnv("dummy", "default"), "value");
    }

    @Test
    public void shouldGetDefaultIfNoSystemOrEnvFound() {
        System.setProperty("dummy", "value");
        assertEquals(ConfigUtils.systemOrEnv("dummy1", "default"), "default");
    }

}