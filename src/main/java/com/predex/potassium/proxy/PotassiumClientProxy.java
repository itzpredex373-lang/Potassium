package com.predex.potassium.proxy;

import com.predex.potassium.optimization.system.SystemPerformanceScheduler;
import com.predex.potassium.PotassiumEventHandler;
import net.minecraftforge.common.MinecraftForge;

public final class PotassiumClientProxy extends PotassiumProxy {
    @Override
    public void registerClientHooks() {
        MinecraftForge.EVENT_BUS.register(new PotassiumEventHandler());
        MinecraftForge.EVENT_BUS.register(new SystemPerformanceScheduler());
    }
}
