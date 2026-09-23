package com.predex.potassium.optimization.chunks;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Client chunk-work scheduler.
 *
 * Part 2 intentionally keeps the scheduler separate from Minecraft's internal
 * RenderGlobal/RenderChunk implementation. That avoids invasive coremod/ASM
 * hooks in the first pass while giving later render hooks a bounded budget.
 */
public final class ChunkRenderScheduler {

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        ChunkUpdateOptimizer.beginTick();

        if (!ChunkOptimizer.isEnabled()) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.theWorld == null || minecraft.thePlayer == null) {
            return;
        }

        // Reserve a small bounded amount of work for future chunk-render hooks.
        // This loop intentionally does not touch loaded-chunk collections.
        for (int i = 0; i < 1 && ChunkUpdateOptimizer.tryAcquireUpdateSlot(); i++) {
            // Future RenderChunk scheduling hook.
        }
    }
}
