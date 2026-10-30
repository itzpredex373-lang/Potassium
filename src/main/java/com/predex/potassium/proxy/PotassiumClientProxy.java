package com.predex.potassium.proxy;

import com.predex.potassium.client.PotassiumGuiHandler;
import com.predex.potassium.client.PotassiumQolHud;
import com.predex.potassium.optimization.system.SystemPerformanceScheduler;
import com.predex.potassium.PotassiumEventHandler;
import net.minecraftforge.common.MinecraftForge;

public final class PotassiumClientProxy extends PotassiumProxy {
    @Override
    public void registerClientHooks() {
        MinecraftForge.EVENT_BUS.register(new PotassiumEventHandler());
        MinecraftForge.EVENT_BUS.register(new SystemPerformanceScheduler());
        MinecraftForge.EVENT_BUS.register(new PotassiumGuiHandler());
        MinecraftForge.EVENT_BUS.register(new PotassiumQolHud());
    }
}
