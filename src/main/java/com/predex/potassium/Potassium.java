package com.predex.potassium;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.rendering.EntityRenderOptimizer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Potassium.MOD_ID, name = Potassium.NAME, version = Potassium.VERSION, acceptedMinecraftVersions = "[1.8.9]")
public final class Potassium {
    public static final String MOD_ID = "potassium";
    public static final String NAME = "Potassium";
    public static final String VERSION = "0.1.0";

    private static Potassium instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        instance = this;
        PotassiumConfig.init(event.getSuggestedConfigurationFile());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new PotassiumEventHandler());

        if (FMLCommonHandler.instance().getSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(new EntityRenderOptimizer());
        }
    }

    public static Potassium getInstance() {
        return instance;
    }
}
