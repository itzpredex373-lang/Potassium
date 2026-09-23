package com.predex.potassium.proxy;

import com.predex.potassium.optimization.rendering.EntityRenderOptimizer;
import com.predex.potassium.optimization.chunks.ChunkRenderScheduler;
import net.minecraftforge.common.MinecraftForge;

/**
 * Client-only initialization.
 */
public final class PotassiumClientProxy extends PotassiumProxy {
    @Override
    public void registerClientHooks() {
        MinecraftForge.EVENT_BUS.register(new EntityRenderOptimizer());
        MinecraftForge.EVENT_BUS.register(new ChunkRenderScheduler());
    }
}
