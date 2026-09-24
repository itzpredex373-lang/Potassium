package com.predex.potassium.core;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.chunks.ChunkUpdateOptimizer;
import com.predex.potassium.optimization.compat.CompatibilityManager;
import com.predex.potassium.optimization.benchmark.PerformanceTelemetry;
import com.predex.potassium.optimization.benchmark.PotassiumDevDiagnostics;
import com.predex.potassium.optimization.particles.ParticleOptimizer;
import com.predex.potassium.optimization.rendering.BlockRenderOptimizer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public final class PotassiumCoreHooks {
    private PotassiumCoreHooks() {}

    public static boolean allowParticleSpawn() {
        PotassiumDevDiagnostics.particleHookCalls++;
        if (!CompatibilityManager.allowRiskyHooks()) return true;
        boolean allowed = !PotassiumConfig.enabled || ParticleOptimizer.tryAcquire();
        if (!allowed) PerformanceTelemetry.skippedParticle();
        return allowed;
    }

    public static boolean allowTessellatorDraw() {
        PotassiumDevDiagnostics.tessellatorHookCalls++;
        if (!CompatibilityManager.allowRiskyHooks()) return true;
        if (!PotassiumConfig.enabled || !PotassiumConfig.skipEmptyDrawCalls) return true;

        try {
            WorldRenderer renderer = Tessellator.getInstance().getWorldRenderer();
            boolean allowed = renderer == null || renderer.getVertexCount() > 0;
            if (!allowed) PerformanceTelemetry.skippedDraw();
            return allowed;
        } catch (Throwable ignored) {
            return true;
        }
    }

    public static boolean allowChunkRendererUpdate() {
        PotassiumDevDiagnostics.chunkHookCalls++;
        if (!CompatibilityManager.allowRiskyHooks()) return true;
        if (!PotassiumConfig.enabled || !PotassiumConfig.rendererCoreHooks || !PotassiumConfig.optimizeChunkUpdates) return true;
        boolean allowed = ChunkUpdateOptimizer.shouldRunRendererUpdate();
        if (!allowed) PerformanceTelemetry.skippedChunk();
        return allowed;
    }

    public static boolean skipFullyOccludedBlock(
            IBlockAccess world, IBlockState state, BlockPos pos) {
        PotassiumDevDiagnostics.blockHookCalls++;
        if (!CompatibilityManager.allowRiskyHooks()) return false;
        if (!PotassiumConfig.enabled || !PotassiumConfig.rendererCoreHooks || !PotassiumConfig.blockFaceCulling) return false;
        return BlockRenderOptimizer.isFullyOccluded(world, state, pos);
    }
}
