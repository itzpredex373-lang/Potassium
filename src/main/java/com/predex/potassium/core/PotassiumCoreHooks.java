package com.predex.potassium.core;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.chunks.ChunkUpdateOptimizer;
import com.predex.potassium.optimization.particles.ParticleOptimizer;
import com.predex.potassium.optimization.rendering.BlockRenderOptimizer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public final class PotassiumCoreHooks {
    private PotassiumCoreHooks() {}

    public static boolean allowParticleSpawn() {
        return !PotassiumConfig.enabled || ParticleOptimizer.tryAcquire();
    }

    public static boolean allowTessellatorDraw() {
        if (!PotassiumConfig.enabled) return true;

        try {
            WorldRenderer renderer = Tessellator.getInstance().getWorldRenderer();
            return renderer == null || renderer.getVertexCount() > 0;
        } catch (Throwable ignored) {
            return true;
        }
    }

    public static boolean allowChunkRendererUpdate() {
        if (!PotassiumConfig.enabled || !PotassiumConfig.optimizeChunkUpdates) return true;
        return ChunkUpdateOptimizer.shouldRunRendererUpdate();
    }

    public static boolean skipFullyOccludedBlock(
            IBlockAccess world, IBlockState state, BlockPos pos) {
        if (!PotassiumConfig.enabled || !PotassiumConfig.blockFaceCulling) return false;
        return BlockRenderOptimizer.isFullyOccluded(world, state, pos);
    }
}
