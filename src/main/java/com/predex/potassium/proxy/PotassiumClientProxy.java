package com.predex.potassium.proxy;

import com.predex.potassium.optimization.rendering.EntityRenderOptimizer;
import com.predex.potassium.optimization.entities.EntityUpdateHandler;
import com.predex.potassium.optimization.particles.ParticleClientScheduler;
import com.predex.potassium.optimization.chunks.ChunkRenderScheduler;
import com.predex.potassium.optimization.system.SystemPerformanceScheduler;
import com.predex.potassium.PotassiumEventHandler;
import net.minecraftforge.common.MinecraftForge;

public final class PotassiumClientProxy extends PotassiumProxy {
    @Override
    public void registerClientHooks() {
        MinecraftForge.EVENT_BUS.register(new PotassiumEventHandler());
        MinecraftForge.EVENT_BUS.register(new SystemPerformanceScheduler());
        MinecraftForge.EVENT_BUS.register(new EntityRenderOptimizer());
        MinecraftForge.EVENT_BUS.register(new EntityUpdateHandler());
        MinecraftForge.EVENT_BUS.register(new ParticleClientScheduler());
        MinecraftForge.EVENT_BUS.register(new ChunkRenderScheduler());
    }
}
