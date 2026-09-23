package com.predex.potassium;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.proxy.PotassiumProxy;
import com.predex.potassium.optimization.profile.PerformanceProfileManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Potassium.MOD_ID, name = Potassium.NAME, version = Potassium.VERSION, acceptedMinecraftVersions = "[1.8.9]")
public final class Potassium {
    public static final String MOD_ID = "potassium";
    public static final String NAME = "Potassium";
    public static final String VERSION = "0.1.0";

    @SidedProxy(
            clientSide = "com.predex.potassium.proxy.PotassiumClientProxy",
            serverSide = "com.predex.potassium.proxy.PotassiumProxy")
    private static PotassiumProxy proxy;

    private static Potassium instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        instance = this;
        PotassiumConfig.init(event.getSuggestedConfigurationFile());
        PerformanceProfileManager.applyConfiguredProfile();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new PotassiumEventHandler());
        proxy.registerClientHooks();
    }

    public static Potassium getInstance() {
        return instance;
    }
}
