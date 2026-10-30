package com.predex.potassium;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Minimal Forge entry point.
 *
 * Legacy renderer, chunk pipeline, telemetry, profile, HUD, and experimental
 * optimization implementations were removed. New features must be independently
 * useful, OptiFine-safe, measurable, and tested before they are added.
 */
@Mod(modid = Potassium.MOD_ID, name = Potassium.NAME, version = Potassium.VERSION,
        acceptedMinecraftVersions = "[1.8.9]")
public final class Potassium {
    public static final String MOD_ID = "potassium";
    public static final String NAME = "Potassium: OptiDex";
    public static final String VERSION = "OptiDex";
    private static final Logger LOGGER = LogManager.getLogger("OptiDex");

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("{} loaded for Minecraft 1.8.9 Forge. Legacy optimization systems are removed.",
                NAME);
    }
}
