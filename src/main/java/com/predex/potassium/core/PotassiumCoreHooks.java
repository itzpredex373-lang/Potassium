package com.predex.potassium.core;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;
import com.predex.potassium.optimization.chunks.ChunkRenderPipeline;
import com.predex.potassium.optimization.chunks.PotassiumChunkBuildController;
import com.predex.potassium.optimization.chunks.PotassiumChunkMeshCache;
import com.predex.potassium.optimization.chunks.ChunkUpdateOptimizer;
import com.predex.potassium.optimization.chunks.PotassiumRealChunkMeshEngine;
import com.predex.potassium.optimization.chunks.PotassiumGpuRegionManager;
import com.predex.potassium.optimization.compat.CompatibilityManager;
import com.predex.potassium.optimization.benchmark.PerformanceTelemetry;
import com.predex.potassium.optimization.particles.ParticleOptimizer;
import com.predex.potassium.optimization.rendering.BlockRenderOptimizer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.VboRenderList;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public final class PotassiumCoreHooks {
    private PotassiumCoreHooks() {}

    public static boolean allowEntityRender(Entity entity) {
        if (!PerformanceManager.isOptimizationEnabled()) return true;
        if (!CompatibilityManager.allowRiskyHooks()) return true;
        if (!PotassiumConfig.rendererCoreHooks || !PotassiumConfig.fastRender) return true;
        return com.predex.potassium.optimization.rendering.RenderOptimizer.shouldRenderEntity(entity);
    }

    public static boolean allowParticleSpawn() {
        if (!PerformanceManager.isOptimizationEnabled()) return true;
        if (!CompatibilityManager.allowRiskyHooks()) return true;
        boolean allowed = ParticleOptimizer.tryAcquire();
        if (!allowed) PerformanceTelemetry.skippedParticle();
        return allowed;
    }

    public static boolean allowTessellatorDraw() {
        if (!PerformanceManager.isOptimizationEnabled()) return true;
        if (!CompatibilityManager.allowRiskyHooks()) return true;
        if (!PotassiumConfig.skipEmptyDrawCalls) return true;

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
        if (!PerformanceManager.isOptimizationEnabled()) return true;
        if (!CompatibilityManager.allowRiskyHooks()) return true;
        if (!PotassiumConfig.rendererCoreHooks || !PotassiumConfig.optimizeChunkUpdates) return true;
        boolean allowed = ChunkUpdateOptimizer.shouldRunRendererUpdate();
        if (!allowed) PerformanceTelemetry.skippedChunk();
        return allowed;
    }

    public static void beginChunkRenderPipeline(long finishTimeNano) {
        if (!PerformanceManager.isOptimizationEnabled()
                || !PotassiumConfig.rendererCoreHooks
                || !PotassiumConfig.optimizeChunkUpdates) {
            return;
        }
        ChunkRenderPipeline.beginUpdateWindow(finishTimeNano);
    }

    public static void finishChunkRenderPipeline() {
        ChunkRenderPipeline.finishUpdateWindow();
    }

    public static boolean renderCustomChunkLayer(
            VboRenderList renderList, EnumWorldBlockLayer layer) {
        if (!PerformanceManager.isOptimizationEnabled()
                || !CompatibilityManager.allowRiskyHooks()
                || !PotassiumConfig.rendererCoreHooks) {
            return false;
        }
        try {
            return PotassiumGpuRegionManager.renderLayer(renderList, layer);
        } catch (Throwable ignored) {
            CompatibilityManager.recordRendererFailure();
            return false;
        }
    }

    public static boolean allowChunkDispatch(
            ChunkRenderDispatcher dispatcher, RenderChunk renderChunk) {
        if (!PerformanceManager.isOptimizationEnabled()) return true;
        if (!CompatibilityManager.allowRiskyHooks()) return true;
        return ChunkRenderPipeline.allowChunkDispatch(dispatcher, renderChunk);
    }

    public static boolean rebuildChunkWithPotassium(
            RenderChunk renderChunk,
            float cameraX,
            float cameraY,
            float cameraZ,
            net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator generator) {
        if (!PerformanceManager.isOptimizationEnabled()
                || !CompatibilityManager.allowRiskyHooks()
                || !PotassiumConfig.rendererCoreHooks
                || !PotassiumConfig.customMeshPreparation
                || !PotassiumConfig.meshUploadPipeline
                || !PotassiumConfig.optimizeChunkUpdates) {
            return false;
        }

        try {
            PotassiumChunkMeshCache.markBuilding(renderChunk);
            return PotassiumRealChunkMeshEngine.rebuild(
                    renderChunk, cameraX, cameraY, cameraZ, generator);
        } catch (Throwable ignored) {
            // The transformer must never make vanilla chunk rebuilding unavailable.
            return false;
        }
    }

    public static void finishChunkMeshUpload(RenderChunk renderChunk) {
        if (renderChunk == null) return;
        try {
            PotassiumChunkMeshCache.markClean(renderChunk);
        } catch (Throwable ignored) {
        }
    }

    public static void beginChunkBuildTick() {
        if (PerformanceManager.isOptimizationEnabled()) {
            PotassiumChunkBuildController.beginTick();
        }
    }

    public static boolean allowChunkBuild(RenderChunk renderChunk) {
        if (!PerformanceManager.isOptimizationEnabled()) return true;
        if (!CompatibilityManager.allowRiskyHooks()) return true;
        return PotassiumChunkBuildController.allowBuild(renderChunk);
    }

    public static void finishChunkBuild(RenderChunk renderChunk) {
        PotassiumChunkBuildController.finishBuild(renderChunk);
    }

    public static boolean allowChunkInvalidation(RenderChunk renderChunk, boolean requested) {
        if (!requested) return true;
        if (!PerformanceManager.isOptimizationEnabled()
                || !PotassiumConfig.rendererCoreHooks
                || !PotassiumConfig.optimizeChunkUpdates
                || renderChunk == null) {
            return true;
        }

        // setNeedsUpdate(true) is frequently called by neighboring block
        // updates. Once the chunk is already dirty, another invalidation adds
        // no useful work and only increases queue pressure.
        try {
            boolean alreadyDirty = renderChunk.isNeedsUpdate();
            if (!alreadyDirty) {
                com.predex.potassium.optimization.chunks.PotassiumChunkMeshCache.markDirty(renderChunk);
                com.predex.potassium.optimization.chunks.PotassiumRenderSectionManager.markDirty(renderChunk);
            }
            return !alreadyDirty;
        } catch (Throwable ignored) {
            return true;
        }
    }

    public static boolean skipFullyOccludedBlock(
            IBlockAccess world, IBlockState state, BlockPos pos) {
        if (!PerformanceManager.isOptimizationEnabled()) return false;
        if (!CompatibilityManager.allowRiskyHooks()) return false;
        if (!PotassiumConfig.rendererCoreHooks || !PotassiumConfig.blockFaceCulling) return false;
        return BlockRenderOptimizer.isFullyOccluded(world, state, pos);
    }
}
