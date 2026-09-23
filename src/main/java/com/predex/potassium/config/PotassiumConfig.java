package com.predex.potassium.config;

import net.minecraftforge.common.config.Configuration;
import java.io.File;

public final class PotassiumConfig {
    private static Configuration configuration;

    public static boolean enabled = true;
    public static boolean lowMemoryMode = true;
    public static boolean reduceParticles = true;
    public static boolean reduceEntityUpdates = false;

    private PotassiumConfig() {}

    public static void init(File file) {
        configuration = new Configuration(file);
        load();
    }

    public static void load() {
        if (configuration == null) return;
        configuration.load();

        enabled = configuration.getBoolean("enabled", "general", true,
                "Master switch for Potassium optimizations.");
        lowMemoryMode = configuration.getBoolean("lowMemoryMode", "performance", true,
                "Conservative settings intended for low-memory devices.");
        reduceParticles = configuration.getBoolean("reduceParticles", "performance", true,
                "Allows Potassium to reduce unnecessary particle work.");
        reduceEntityUpdates = configuration.getBoolean("reduceEntityUpdates", "performance", false,
                "Experimental entity-update optimization. Disabled by default.");

        if (configuration.hasChanged()) configuration.save();
    }

    public static Configuration getConfiguration() {
        return configuration;
    }
}
