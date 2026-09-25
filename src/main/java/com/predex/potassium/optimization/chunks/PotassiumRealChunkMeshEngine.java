package com.predex.potassium.optimization.chunks;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.RegionRenderCache;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

/**
 * Real client-side chunk mesh compiler for Minecraft 1.8.9.
 *
 * This is not a placeholder queue: it performs the complete 16x16x16 block
 * traversal, block-model/fluid tessellation, layer separation, translucent
 * sort-state generation and VisGraph visibility compilation. The resulting
 * WorldRenderer buffers are handed to the normal 1.8.9 upload path, preserving
 * the vanilla GL-thread ownership contract.
 *
 * The engine is deliberately compatible with ChunkCompileTaskGenerator so a
 * failed/custom-disabled build can fall back to vanilla rebuildChunk().
 */
public final class PotassiumRealChunkMeshEngine {
    private static final int CHUNK_SIZE = 16;
    private static final int BORDER = 1;

    private static volatile long builds;
    private static volatile long blocksVisited;
    private static volatile long blocksRendered;
    private static volatile long emptyBlocks;
    private static volatile long failedBuilds;
    private static volatile long lastBuildNanos;

    private PotassiumRealChunkMeshEngine() {}

    public static boolean rebuild(RenderChunk renderChunk,
                                  float cameraX,
                                  float cameraY,
                                  float cameraZ,
                                  ChunkCompileTaskGenerator generator) {
        if (!isEnabled() || renderChunk == null || generator == null) {
            return false;
        }

        if (generator.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING) {
            return false;
        }

        final BlockPos origin = renderChunk.getPosition();
        if (origin == null) {
            return false;
        }

        final long start = System.nanoTime();
        final CompiledChunk compiledChunk = new CompiledChunk();
        final boolean[] started = new boolean[EnumWorldBlockLayer.values().length];
        final boolean[] used = new boolean[EnumWorldBlockLayer.values().length];
        final VisGraph visibility = new VisGraph();
        final Set<TileEntity> tileEntities = new HashSet<TileEntity>();

        generator.getLock().lock();
        try {
            if (generator.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING) {
                return false;
            }

            World world = Minecraft.getMinecraft().theWorld;
            if (world == null) {
                return false;
            }

            IBlockAccess blockAccess = new RegionRenderCache(
                    world,
                    origin.add(-BORDER, -BORDER, -BORDER),
                    origin.add(CHUNK_SIZE - 1 + BORDER, CHUNK_SIZE - 1 + BORDER, CHUNK_SIZE - 1 + BORDER),
                    BORDER);

            generator.setCompiledChunk(compiledChunk);

            BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            if (dispatcher == null) {
                return false;
            }

            for (BlockPos.MutableBlockPos pos :
                    BlockPos.getAllInBoxMutable(origin, origin.add(CHUNK_SIZE - 1, CHUNK_SIZE - 1, CHUNK_SIZE - 1))) {

                ++blocksVisited;

                IBlockState state = blockAccess.getBlockState(pos);
                Block block = state.getBlock();

                if (block.isOpaqueCube()) {
                    visibility.func_178606_a(pos);
                }

                if (block.hasTileEntity()) {
                    TileEntity tileEntity = blockAccess.getTileEntity(new BlockPos(pos));
                    if (tileEntity != null) {
                        TileEntitySpecialRenderer<TileEntity> specialRenderer =
                                TileEntityRendererDispatcher.instance.getSpecialRenderer(tileEntity);

                        if (specialRenderer != null) {
                            tileEntities.add(tileEntity);
                            compiledChunk.addTileEntity(tileEntity);
                        }
                    }
                }

                int renderType = block.getRenderType();
                if (renderType == -1) {
                    ++emptyBlocks;
                    continue;
                }

                EnumWorldBlockLayer layer = block.getBlockLayer();
                int layerId = layer.ordinal();
                WorldRenderer renderer =
                        generator.getRegionRenderCacheBuilder().getWorldRendererByLayerId(layerId);

                if (!compiledChunk.isLayerStarted(layer)) {
                    compiledChunk.setLayerStarted(layer);
                    renderer.begin(7, DefaultVertexFormats.BLOCK);
                    renderer.setTranslation(-origin.getX(), -origin.getY(), -origin.getZ());
                    started[layerId] = true;
                }

                if (dispatcher.renderBlock(state, pos, blockAccess, renderer)) {
                    used[layerId] = true;
                    ++blocksRendered;
                }
            }

            for (EnumWorldBlockLayer layer : EnumWorldBlockLayer.values()) {
                int layerId = layer.ordinal();
                WorldRenderer renderer =
                        generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(layer);

                if (used[layerId]) {
                    compiledChunk.setLayerUsed(layer);
                }

                if (started[layerId]) {
                    if (layer == EnumWorldBlockLayer.TRANSLUCENT && !compiledChunk.isLayerEmpty(layer)) {
                        renderer.sortVertexData(cameraX, cameraY, cameraZ);
                        compiledChunk.setState(renderer.getVertexState());
                    }

                    renderer.finishDrawing();
                    renderer.setTranslation(0.0D, 0.0D, 0.0D);
                }
            }

            compiledChunk.setVisibility(visibility.computeVisibility());

            updateTileEntities(renderChunk, tileEntities);

            ++builds;
            lastBuildNanos = System.nanoTime() - start;
            PotassiumChunkMeshCache.markUploadPending(renderChunk);
            return true;
        } catch (Throwable failure) {
            ++failedBuilds;
            cleanupFailedBuffers(generator, started);
            return false;
        } finally {
            generator.getLock().unlock();
        }
    }

    private static void cleanupFailedBuffers(ChunkCompileTaskGenerator generator, boolean[] started) {
        try {
            for (EnumWorldBlockLayer layer : EnumWorldBlockLayer.values()) {
                int id = layer.ordinal();
                if (!started[id]) {
                    continue;
                }

                WorldRenderer renderer =
                        generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(layer);

                try {
                    renderer.finishDrawing();
                } catch (Throwable ignored) {
                }

                try {
                    renderer.setTranslation(0.0D, 0.0D, 0.0D);
                } catch (Throwable ignored) {
                }
            }
        } catch (Throwable ignored) {
        }
    }

    private static void updateTileEntities(RenderChunk renderChunk, Set<TileEntity> current) {
        try {
            Set<TileEntity> oldSet = new HashSet<TileEntity>(
                    renderChunk.getCompiledChunk().getTileEntities());
            Set<TileEntity> removed = new HashSet<TileEntity>(oldSet);
            Set<TileEntity> added = new HashSet<TileEntity>(current);
            removed.removeAll(current);
            added.removeAll(oldSet);

            if (!removed.isEmpty() || !added.isEmpty()) {
                Minecraft.getMinecraft().renderGlobal.updateTileEntities(removed, added);
            }
        } catch (Throwable ignored) {
            // Tile-entity bookkeeping must never invalidate an otherwise valid mesh.
        }
    }

    private static boolean isEnabled() {
        return PerformanceManager.isOptimizationEnabled()
                && PotassiumConfig.rendererCoreHooks
                && PotassiumConfig.optimizeChunkUpdates
                && PotassiumConfig.customMeshPreparation
                && PotassiumConfig.meshUploadPipeline;
    }

    public static long getBuilds() {
        return builds;
    }

    public static long getBlocksVisited() {
        return blocksVisited;
    }

    public static long getBlocksRendered() {
        return blocksRendered;
    }

    public static long getEmptyBlocks() {
        return emptyBlocks;
    }

    public static long getFailedBuilds() {
        return failedBuilds;
    }

    public static long getLastBuildNanos() {
        return lastBuildNanos;
    }

    public static void resetStats() {
        builds = 0L;
        blocksVisited = 0L;
        blocksRendered = 0L;
        emptyBlocks = 0L;
        failedBuilds = 0L;
        lastBuildNanos = 0L;
    }
}
