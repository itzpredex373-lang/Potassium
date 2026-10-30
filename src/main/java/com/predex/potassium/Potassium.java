package com.predex.potassium;

import com.predex.potassium.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * OptiFine-safe companion foundation. This release adds diagnostics only;
 * it does not install renderer hooks or claim FPS improvements.
 */
@Mod(modid = Potassium.MOD_ID, name = Potassium.NAME, version = Potassium.VERSION,
        acceptedMinecraftVersions = "[1.8.9]")
public final class Potassium {
    public static final String MOD_ID = "potassium";
    public static final String NAME = "Potassium: OptiDex";
    public static final String VERSION = "OptiDex";
    private static final Logger LOGGER = LogManager.getLogger("OptiDex");

    @SidedProxy(clientSide = "com.predex.potassium.proxy.ClientProxy",
            serverSide = "com.predex.potassium.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
        LOGGER.info("{} loaded. Renderer and chunk optimization hooks are not installed.", NAME);
    }
}
